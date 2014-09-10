
# Resources

Resources are the basic unit of data in ActivityInfo. Each resource has an identity, an owner, 
and zero or more properties. 
 
Resources are analogous to:
* A _resource_ in rdfs/owl
* A single _row_ in a relational database
* A _node_ in a graph database
* A _document_ in many "no-SQL" databases like Mongo DB
* An _entity_ in datatomic

Resources may be created by users, or defined by the application.

# Access Control Rules

Each resource may have zero or more access control rules that determine which operations a user may perform
on a resource.

# Resource Owners

Each resource, except the root resource, is "owned" by exactly one other resource. 

A resource inherits it's access control rules from its owner.

# Workspaces

User-created resources are organized into _workspaces_. Workspaces are logical grouping of resources within which
all transactions have serialized consistency. 

Each workspace has its own version number. Each time there is a change to a resource within the workspace, it's 
version number is incremented by the server. This allows clients to synchronize their copy of a workspace by comparing
the local version number with the 

Resources that have been changed locally but not yet assigned a new version by the server are called "uncommitted"


