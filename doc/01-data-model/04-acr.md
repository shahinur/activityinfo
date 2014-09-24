
# Access Control Rules

Resources may have zero or more Access Control Rules (ACR) which define the operations that individual users
or user groups are permitted to perform. 

The basic operations are:

1. View
2. Edit

Additional, ACRs may grant "ownership" to users. Users who own resources may modify ACRs on resources they own.

# Inheritance

Resources are organized in a file-system-like tree, where a resource's ownerId specifies its parent within this tree.

Resources inherit ACRs from their parents, according to the following rules.

1. Ownership is only additive. A user who is defined as the owner of a resource is considered an owner of all
   descendants of this resource.
   
2. For non-owners, permissions are evaluated based on the closest defined ancestor that has an ACR.

