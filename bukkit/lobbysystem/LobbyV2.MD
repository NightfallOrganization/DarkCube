Format:

```
{
    "<key>"("2x1"): {
        "displayName": "<displayName>"({"text":"Mirror 4x2"}),
        "playingPlayers": <onlinePlayers>,
        "maxPlayingPlayers": <maxPlayers>,
        "spectatingPlayers": <spectatingPlayers>,
        "gameState": <gameState>,
        "document": {
            <dataToSend>
        }
    }
}
```

Protocol:

Everything on `Channel: "darkcube_lobbysystem_v2"`

Lobby asks service to start a connection request for a player

```
Message: "start_connection_request"
DataBuf: UUID requestId, UUID playerUUID, Document protocolDocument
```

Then the service response with a status packet:

```
Message: "connection_request_status"
DataBuf: UUID requestId, int status (1 Success, 0 Failure)(, String message)
```

In case of failure a message is appended in form of `String`, the request stops

Otherwise the service loads the data required indicated by the `protocolDocument` and returns status packet:

```
Message: "connection_request_status"
DataBuf: UUID requestId, int status (2 Success, 0 Failure)(, String message)
```

In case of failure a message is appended in form of `String`, the request stops

Otherwise the lobby tries to send the player to the service

The timeout for the connection request should be picked by the service. About 5 seconds should be a good timeout

After that timeout the service should:

- unload the data associated with the request
- disallow any players trying to connect
