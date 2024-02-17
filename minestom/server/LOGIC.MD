# Logic for Chunk System 👀️

* **TicketType:**

  * Name (String)
  * Time-To-Live (Ticks, int)
* **ChunkTicket**

  * ChunkLevel
  * TickCreated
  * TicketType
* **ChunkLevel:**

  * int
  * Level 1 means radius 0 (ex. 1 chunk)
  * Level 0 means not loaded
  * Level 2 means radius 1
  * Level 3 means radius 2
* **LoadStage:**

  * A stage in WorldGeneration that is required
  * This may be just basic terrain generation, or everything after block population phases
  * Very free definition
  * Makes it quicker to query chunks if they are not required to generate fully
* **Load Chunk (TicketType, ChunkLevel, LoadStage):**

  * LoadChunk (LoadStage, () -> addTicket(new ChunkTicket(TicketType, ChunkLevel, currentTick))
* **Load Chunk Task (LoadStage, Callback)**

  * Check if chunk already exists and is loaded -> work on that
    * Otherwise create a new chunk instance and cache that. Then work on that
  * The cached instance may be in a bad state. It need not be fully generated, for example
    * Make sure the **LoadStage** is correct. Generate everything neccessary to get the Chunk to at least the given **LoadStage**.
    * May generate further than the given **LoadStage**. The Implementation may fully generate the chunk, even before the future returns.
  * The chunk is cached and the **LoadStage** is correct at this point
  * Execute **Callback**
  * Add Chunk to **UpdateQueue**
  * Complete the future
  * Make sure to correctly synchronize everything
* **Unload Chunk**

  * Can not be called from the server itself. The Chunk System has to unload on its own, to ensure stability
  * Throws UnsupportedOperationException
* **Update All Chunks**

  * Take first Chunk from **UpdateQueue**
  * **UpdateChunk(Chunk)**
* **UpdateChunk (Chunk)**

  * Get Ticket with highest Level that isn't propagated
  * If Ticket Exists
    * NewLevel = max(CalculatePropagated(), ticket.level)
  * Else
    * NewLevel = CalculatePropagated()
  * If NewLevel <= 0 **unloadChunkInternal**
* **UnloadChunkInternal**

  * Save Callbacks
  * Events
  * StopTicking
