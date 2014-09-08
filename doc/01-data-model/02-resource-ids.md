

# Resource IDs

## Requirements

* Disconnected clients must be able to generate IDs themselves to enable clients to function offline
* 

## Structure

To the extent possible, Resource IDs should be considered opaque URL-safe strings of varying length.
 
Resource IDs are used to provide resources with a unique, persistent identity and should not be
considered to have semantic content. Semantic content, such as a person's name, or a URL, can change over time, 
(or be corrected) while a resource's identity must remain stable to ensure that disconnected clients
can talk about the same object.

## Generation

### Application-defined resources

Application-defined resources include FormClasses for models defined by ActivityInfo, such as Workspaces,
Folder, Access Control Rules, etc.

Application-defined resource IDs must begin with the '_' character.


```
_applicationIdentifier
```

### User-defined resources

User-defined resources must be generated independently of the server to enable offline clients to function. To
avoid ID collisions, user-generated resources are generated using a scheme inspired by
[CUIDs](https://github.com/ericelliott/cuid). 

Cuids are composed of several elements chosen to reduce the risk of collision, both in the context of 
a large number of distributed clients, as well as the context of individual clients that may generate 
a large number of ids in a short period as part of an import process, for example.

Cuids are generated from four parts:
* Client ID: a 64-bit integer (provided by the server upon the client's first connection)
* Random: Random number in the range [0, 62^2] 
* Timestamp: number of _seconds_ since the unix time when the client id was issued
* Counter: increasing counter that rolls over at 2^31.
  



