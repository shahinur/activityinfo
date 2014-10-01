# Resource deletions

## Requirements

* Resources can be deleted
* The server retains the knowledge that the resource used to exist but was deleted
* Deletions can be communicated to clients
* Deletion is a transitive operation

## Theory

* Whenever a resource is deleted, a new and final version of the resource is stored which
  represents the fact that the resource is now deleted. 
* Whenever a client requests the resource, the deletion of the resource will be communicated back with the 
  status code 410 (GONE). 
* Deleted resources are also communicated to clients that are synchronizing with the server. 
* Putting an updated (non-deleted) version of a deleted resource is not allowed. 
* Creating a new resource with the same ID as a deleted resource is not allowed either.
* Deleting an already deleted resource is a no-op. 
* If any of a resource's (direct or indirect) parents is marked as deleted, the resource itself is considered to be 
  deleted as well, even if it is not marked as such itself.

## Representation of resources in HRD

* Deletion is considered a change in state, so a new Snapshot is created for the resource.
* A snapshot of a deleted resource has no properties and has a 'deleted' flag set to true.
* When resources are deleted, the corresponding LatestContent entity is removed so that they
  do not appear in indices over LatestContent.
