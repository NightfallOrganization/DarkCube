/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.util.communication;

public enum EnumChannelMessage {

	UPDATE_USER,

	REPORT_PENDING_NEW,
	REPORT_GET_NEW_ID,
	REPORT_GET_BY_ID,
	REPORT_LIST_FOR_USER,
	REPORT_SEND_USER_DETAILS,
	REPORT_REMOVE,
	REPORT_GET_ALL,
	BOT_LOG

	;

	private String msg;

	private EnumChannelMessage() {
		this.msg = name().toLowerCase();
	}

	public String getMessage() {
		return msg;
	}
}
