# Resource deletions

## Requirements

* Resources can be deleted
* The server retains the knowledge that the resource used to exist but was deleted
* Deletions can be communicated to clients
* Deletion is a transitive operation

## Theory

Whenever a resource is deleted, a new and final version of the resource is stored which
represents the fact that the resource is now deleted. Whenever a client requests the
resource, the deletion of the resource will be communicated back. Deleted resources are
also communicated to clients that are synchronizing with the server. Putting an updated
(non-deleted) version of a deleted resource is not allowed. Creating a new resource with
the same ID as a deleted resource is not allowed either. Deleting an already deleted
resource is a no-op. If any of a resource's (direct or indirect) parents is marked as deleted,
the resource itself is considered to be deleted as well, even if it is not marked as such itself.

## Practice

Deleted resources are represented by the fact that their properties maps are completely
empty. Methods on the Resource class allow for this to be queried. Whenever a resource is
retrieved from the database, its own deletion status and those of its (direct or indirect)
parents all have to be checked. When a deleted resource is requested, the HTTP status
code for "Gone" is returned. When a list of updated resources is requested, deleted
resources are returned in a JSON representation that closely mirrors their internal
representation.
