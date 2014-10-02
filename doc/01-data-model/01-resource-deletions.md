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
* When resources are deleted, the corresponding LatestContent entity properties are saved as unindexed and
  do not appear in indices over LatestContent.

## Security Considerations

In some rare cases, the fact that a record has been deleted should be considered confidential information. Take,
for example, a security incident that is reported through AI and inadvertently shared with a malicious user with
connections to the aggressor documented in the incident report.
 
If it is the practice of the data owner to delete reports which are considered to be false, the malicious user could
poll AI periodically to determine the status of the incident, even after their access has been revoked.

For this reason, the LatestContent entities of the ACRs which apply to a deleted resource should be left in place.
A GET request for a deleted entity for which the user is not authorized to view should return FORBIDDEN rather than
NOT FOUND, even after that resource has been deleted.

These precautions do not affect the queryXX() methods, resources will not be visible to unauthorized users regardless
of whether they are deleted or not deleted.