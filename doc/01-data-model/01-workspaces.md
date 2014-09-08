
# Workspaces

User data is organized into _workspaces_. Workspaces are logical grouping of resources that have one or
more owners and within which all transactions have serialized consistency. 

Workspaces have a monotonically-increasing version number that provides a total ordering of all changes to 
the workspaces and facilitates synchronization with offline storage in browsers.

