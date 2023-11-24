/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.minestom.server.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Objects;

@SuppressWarnings({"UnnecessaryLabelOnBreakStatement", "CommentedOutCode", "SpellCheckingInspection"})
public final class ConcurrentLinkedNodeDeque<T> {

    private static final int HOPS = 2;
    private static final Node<Object> PREV_TERMINATOR, NEXT_TERMINATOR;
    private static final VarHandle HEAD;
    private static final VarHandle TAIL;
    private static final VarHandle ITEM;
    private static final VarHandle NEXT;
    private static final VarHandle PREV;

    static {
        PREV_TERMINATOR = new Node<>();
        PREV_TERMINATOR.next = PREV_TERMINATOR;
        NEXT_TERMINATOR = new Node<>();
        NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
        try {
            var lookup = MethodHandles.lookup();
            HEAD = lookup.findVarHandle(ConcurrentLinkedNodeDeque.class, "head", Node.class);
            TAIL = lookup.findVarHandle(ConcurrentLinkedNodeDeque.class, "tail", Node.class);
            PREV = lookup.findVarHandle(Node.class, "prev", Node.class);
            NEXT = lookup.findVarHandle(Node.class, "next", Node.class);
            ITEM = lookup.findVarHandle(Node.class, "item", Object.class);
        } catch (ReflectiveOperationException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private volatile Node<T> head;
    private volatile Node<T> tail;

    public ConcurrentLinkedNodeDeque() {
        head = tail = new Node<>();
    }

    public @NotNull Node<T> offerFirst(T item) {
        return linkFirst(item);
    }

    public @NotNull Node<T> offerLast(T item) {
        return linkLast(item);
    }

    public @Nullable T pollFirst() {
        restart:
        for (; ; ) {
            for (Node<T> first = first(), p = first; ; ) {
                final T item;
                if ((item = p.item) != null) {
                    // recheck for linearizability
                    if (first.prev != null) continue restart;
                    if (ITEM.compareAndSet(p, item, null)) {
                        unlinkInternal(p);
                        return item;
                    }
                }
                if (p == (p = p.next)) continue restart;
                if (p == null) {
                    if (first.prev != null) continue restart;
                    return null;
                }
            }
        }
    }

    public @Nullable T pollLast() {
        restart:
        for (; ; ) {
            for (Node<T> last = last(), p = last; ; ) {
                final T item;
                if ((item = p.item) != null) {
                    // recheck for linearizability
                    if (last.next != null) continue restart;
                    if (ITEM.compareAndSet(p, item, null)) {
                        unlinkInternal(p);
                        return item;
                    }
                }
                if (p == (p = p.prev)) continue restart;
                if (p == null) {
                    if (last.next != null) continue restart;
                    return null;
                }
            }
        }
    }

    public T peekFirst() {
        restart:
        for (; ; ) {
            T item;
            Node<T> first = first(), p = first;
            while ((item = p.item) == null) {
                if (p == (p = p.next)) continue restart;
                if (p == null) break;
            }
            // recheck for linearizability
            if (first.prev != null) continue;
            return item;
        }
    }

    /**
     * Removes the first occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the first element {@code e} such that
     * {@code o.equals(e)} (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     *
     * @param o element to be removed from this deque, if present
     * @return {@code true} if the deque contained the specified element
     * @throws NullPointerException if the specified element is null
     */
    public boolean removeFirstOccurrence(Object o) {
        Objects.requireNonNull(o);
        for (Node<T> p = first(); p != null; p = succ(p)) {
            final T item;
            if ((item = p.item) != null && o.equals(item) && ITEM.compareAndSet(p, item, null)) {
                unlinkInternal(p);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the last occurrence of the specified element from this deque.
     * If the deque does not contain the element, it is unchanged.
     * More formally, removes the last element {@code e} such that
     * {@code o.equals(e)} (if such an element exists).
     * Returns {@code true} if this deque contained the specified element
     * (or equivalently, if this deque changed as a result of the call).
     *
     * @param o element to be removed from this deque, if present
     * @return {@code true} if the deque contained the specified element
     * @throws NullPointerException if the specified element is null
     */
    public boolean removeLastOccurrence(Object o) {
        Objects.requireNonNull(o);
        for (Node<T> p = last(); p != null; p = pred(p)) {
            final T item;
            if ((item = p.item) != null && o.equals(item) && ITEM.compareAndSet(p, item, null)) {
                unlinkInternal(p);
                return true;
            }
        }
        return false;
    }

    /**
     * Guarantees that any node which was unlinked before a call to
     * this method will be unreachable from head after it returns.
     * Does not guarantee to eliminate slack, only that head will
     * point to a node that was active while this method was running.
     */
    private void updateHead() {
        // Either head already points to an active node, or we keep
        // trying to cas it to the first node until it does.
        Node<T> h, p, q;
        restartFromHead:
        while ((h = head).item == null && (p = h.prev) != null) {
            for (; ; ) {
                if ((q = p.prev) == null || (q = (p = q).prev) == null) {
                    // It is possible that p is PREV_TERMINATOR,
                    // but if so, the CAS is guaranteed to fail.
                    if (HEAD.compareAndSet(this, h, p)) return;
                    else continue restartFromHead;
                } else if (h != head) continue restartFromHead;
                else p = q;
            }
        }
    }

    /**
     * Guarantees that any node which was unlinked before a call to
     * this method will be unreachable from tail after it returns.
     * Does not guarantee to eliminate slack, only that tail will
     * point to a node that was active while this method was running.
     */
    private void updateTail() {
        // Either tail already points to an active node, or we keep
        // trying to cas it to the last node until it does.
        Node<T> t, p, q;
        restartFromTail:
        while ((t = tail).item == null && (p = t.next) != null) {
            for (; ; ) {
                if ((q = p.next) == null || (q = (p = q).next) == null) {
                    // It is possible that p is NEXT_TERMINATOR,
                    // but if so, the CAS is guaranteed to fail.
                    if (TAIL.compareAndSet(this, t, p)) return;
                    else continue restartFromTail;
                } else if (t != tail) continue restartFromTail;
                else p = q;
            }
        }
    }

    private void skipDeletedPredecessors(Node<T> x) {
        whileActive:
        do {
            Node<T> prev = x.prev;
            // assert prev != null;
            // assert x != NEXT_TERMINATOR;
            // assert x != PREV_TERMINATOR;
            Node<T> p = prev;
            findActive:
            for (; ; ) {
                if (p.item != null) break findActive;
                Node<T> q = p.prev;
                if (q == null) {
                    if (p.next == p) continue whileActive;
                    break findActive;
                } else if (p == q) continue whileActive;
                else p = q;
            }

            // found active CAS target
            if (prev == p || PREV.compareAndSet(x, prev, p)) return;

        } while (x.item != null || x.next == null);
    }

    private void skipDeletedSuccessors(Node<T> x) {
        whileActive:
        do {
            Node<T> next = x.next;
            // assert next != null;
            // assert x != NEXT_TERMINATOR;
            // assert x != PREV_TERMINATOR;
            Node<T> p = next;
            findActive:
            for (; ; ) {
                if (p.item != null) break findActive;
                Node<T> q = p.next;
                if (q == null) {
                    if (p.prev == p) continue whileActive;
                    break findActive;
                } else if (p == q) continue whileActive;
                else p = q;
            }

            // found active CAS target
            if (next == p || NEXT.compareAndSet(x, next, p)) return;

        } while (x.item != null || x.prev == null);
    }

    private Node<T> prevTerminator() {
        return (Node<T>) PREV_TERMINATOR;
    }

    private Node<T> nextTerminator() {
        return (Node<T>) NEXT_TERMINATOR;
    }

    /**
     * Returns the successor of p, or the first node if p.next has been
     * linked to self, which will only be true if traversing with a
     * stale pointer that is now off the list.
     */
    private Node<T> succ(Node<T> p) {
        // TODO: should we skip deleted nodes here?
        if (p == (p = p.next)) p = first();
        return p;
    }

    /**
     * Returns the predecessor of p, or the last node if p.prev has been
     * linked to self, which will only be true if traversing with a
     * stale pointer that is now off the list.
     */
    private Node<T> pred(Node<T> p) {
        if (p == (p = p.prev)) p = last();
        return p;
    }

    /**
     * <p>Returns the last node, the unique node p for which: p.next == null && p.prev != p</p>
     * <p>The returned node may or may not be logically deleted. Guarantees that tail is set to the returned node</p>
     */
    private Node<T> last() {
        restart:
        while (true) {
            for (Node<T> t = tail, p = t, q; ; ) {
                if ((q = p.next) != null && (q = (p = q).next) != null) {
                    p = (t != (t = tail)) ? t : q;
                } else if (p == t || TAIL.compareAndSet(this, t, p)) {
                    return p;
                } else continue restart;
            }
        }
    }

    /**
     * <p>Returns the first node, the unique node p for which: p.prev == null && p.next != p</p>
     * <p>The returned node may or may not be logically deleted. Guarantees that head is set to the returned node</p>
     */
    private Node<T> first() {
        restart:
        while (true) {
            for (Node<T> h = head, p = h, q; ; ) {
                if ((q = p.prev) != null && (q = (p = q).prev) != null) {
                    p = (h != (h = head)) ? h : q;
                } else if (p == h || HEAD.compareAndSet(this, h, p)) {
                    return p;
                } else continue restart;
            }
        }
    }

    /**
     * Links e as first element.
     */
    private Node<T> linkFirst(T e) {
        final Node<T> newNode = new Node<>(Objects.requireNonNull(e), this);

        restartFromHead:
        for (; ; )
            for (Node<T> h = head, p = h, q; ; ) {
                if ((q = p.prev) != null && (q = (p = q).prev) != null)
                    // Check for head updates every other hop.
                    // If p == q, we are sure to follow head instead.
                    p = (h != (h = head)) ? h : q;
                else if (p.next == p) // PREV_TERMINATOR
                    continue restartFromHead;
                else {
                    // p is first node
                    NEXT.set(newNode, p); // CAS piggyback
                    if (PREV.compareAndSet(p, null, newNode)) {
                        // Successful CAS is the linearization point
                        // for e to become an element of this deque,
                        // and for newNode to become "live".
                        if (p != h) // hop two nodes at a time; failure is OK
                            HEAD.weakCompareAndSet(this, h, newNode);
                        return newNode;
                    }
                    // Lost CAS race to another thread; re-read prev
                }
            }
    }

    /**
     * Links e as last element.
     */
    private Node<T> linkLast(T e) {
        final Node<T> newNode = new Node<>(Objects.requireNonNull(e), this);

        restartFromTail:
        for (; ; )
            for (Node<T> t = tail, p = t, q; ; ) {
                if ((q = p.next) != null && (q = (p = q).next) != null)
                    // Check for tail updates every other hop.
                    // If p == q, we are sure to follow tail instead.
                    p = (t != (t = tail)) ? t : q;
                else if (p.prev == p) // NEXT_TERMINATOR
                    continue restartFromTail;
                else {
                    // p is last node
                    PREV.set(newNode, p); // CAS piggyback
                    if (NEXT.compareAndSet(p, null, newNode)) {
                        // Successful CAS is the linearization point
                        // for e to become an element of this deque,
                        // and for newNode to become "live".
                        if (p != t) // hop two nodes at a time; failure is OK
                            TAIL.weakCompareAndSet(this, t, newNode);
                        return newNode;
                    }
                    // Lost CAS race to another thread; re-read next
                }
            }
    }

    /**
     * Unlinks a node that already has its item cleared
     */
    private void unlinkInternal(Node<T> x) {
        // assert x != null;
        // assert x.item == null;
        // assert x != PREV_TERMINATOR;
        // assert x != NEXT_TERMINATOR;

        final Node<T> prev = x.prev;
        final Node<T> next = x.next;
        if (prev == null) {
            unlinkFirst(x, next);
        } else if (next == null) {
            unlinkLast(x, prev);
        } else {
            // Unlink interior node.
            //
            // This is the common case, since a series of polls at the
            // same end will be "interior" removes, except perhaps for
            // the first one, since end nodes cannot be unlinked.
            //
            // At any time, all active nodes are mutually reachable by
            // following a sequence of either next or prev pointers.
            //
            // Our strategy is to find the unique active predecessor
            // and successor of x.  Try to fix up their links so that
            // they point to each other, leaving x unreachable from
            // active nodes.  If successful, and if x has no live
            // predecessor/successor, we additionally try to gc-unlink,
            // leaving active nodes unreachable from x, by rechecking
            // that the status of predecessor and successor are
            // unchanged and ensuring that x is not reachable from
            // tail/head, before setting x's prev/next links to their
            // logical approximate replacements, self/TERMINATOR.
            Node<T> activePred, activeSucc;
            boolean isFirst, isLast;
            int hops = 1;

            // Find active predecessor
            for (Node<T> p = prev; ; ++hops) {
                if (p.item != null) {
                    activePred = p;
                    isFirst = false;
                    break;
                }
                Node<T> q = p.prev;
                if (q == null) {
                    if (p.next == p) return;
                    activePred = p;
                    isFirst = true;
                    break;
                } else if (p == q) return;
                else p = q;
            }

            // Find active successor
            for (Node<T> p = next; ; ++hops) {
                if (p.item != null) {
                    activeSucc = p;
                    isLast = false;
                    break;
                }
                Node<T> q = p.next;
                if (q == null) {
                    if (p.prev == p) return;
                    activeSucc = p;
                    isLast = true;
                    break;
                } else if (p == q) return;
                else p = q;
            }

            // TODO: better HOP heuristics
            if (hops < HOPS
                    // always squeeze out interior deleted nodes
                    && (isFirst | isLast)) return;

            // Squeeze out deleted nodes between activePred and
            // activeSucc, including x.
            skipDeletedSuccessors(activePred);
            skipDeletedPredecessors(activeSucc);

            // Try to gc-unlink, if possible
            if ((isFirst | isLast) &&

                    // Recheck expected state of predecessor and successor
                    (activePred.next == activeSucc) && (activeSucc.prev == activePred) && (isFirst ? activePred.prev == null : activePred.item != null) && (isLast ? activeSucc.next == null : activeSucc.item != null)) {

                updateHead(); // Ensure x is not reachable from head
                updateTail(); // Ensure x is not reachable from tail

                // Finally, actually gc-unlink
                PREV.setRelease(x, isFirst ? prevTerminator() : x);
                NEXT.setRelease(x, isLast ? nextTerminator() : x);
            }
        }
    }

    /**
     * Unlinks non-null first node.
     */
    private void unlinkFirst(Node<T> first, Node<T> next) {
        // assert first != null;
        // assert next != null;
        // assert first.item == null;
        for (Node<T> o = null, p = next, q; ; ) {
            if (p.item != null || (q = p.next) == null) {
                if (o != null && p.prev != p && NEXT.compareAndSet(first, next, p)) {
                    skipDeletedPredecessors(p);
                    if (first.prev == null && (p.next == null || p.item != null) && p.prev == first) {

                        updateHead(); // Ensure o is not reachable from head
                        updateTail(); // Ensure o is not reachable from tail

                        // Finally, actually gc-unlink
                        NEXT.setRelease(o, o);
                        PREV.setRelease(o, prevTerminator());
                    }
                }
                return;
            } else if (p == q) return;
            else {
                o = p;
                p = q;
            }
        }
    }

    /**
     * Unlinks non-null last node.
     */
    private void unlinkLast(Node<T> last, Node<T> prev) {
        // assert last != null;
        // assert prev != null;
        // assert last.item == null;
        for (Node<T> o = null, p = prev, q; ; ) {
            if (p.item != null || (q = p.prev) == null) {
                if (o != null && p.next != p && PREV.compareAndSet(last, prev, p)) {
                    skipDeletedSuccessors(p);
                    if (last.next == null && (p.prev == null || p.item != null) && p.next == last) {

                        updateHead(); // Ensure o is not reachable from head
                        updateTail(); // Ensure o is not reachable from tail

                        // Finally, actually gc-unlink
                        PREV.setRelease(o, o);
                        NEXT.setRelease(o, nextTerminator());
                    }
                }
                return;
            } else if (p == q) return;
            else {
                o = p;
                p = q;
            }
        }
    }

    public static final class Node<T> {
        private final ConcurrentLinkedNodeDeque<T> deque;
        private volatile Node<T> prev;
        private volatile Node<T> next;
        private volatile T item;

        private Node(T item, ConcurrentLinkedNodeDeque<T> deque) {
            this.deque = deque;
            ITEM.set(this, item);
        }

        private Node() {
            this.deque = null;
        }

        /**
         * @return true if the node was unlinked, false if it could not be unlinked because it isn't valid or already has been unlinked
         */
        public boolean unlink() {
            if (deque == null) return false; // Someone tries to unlink PREV_TERMINATOR or NEXT_TERMINATOR
            final T item = this.item;
            if (ITEM.compareAndSet(this, item, null)) {
                deque.unlinkInternal(this);
                return true;
            }
            return false;
        }
    }
}
