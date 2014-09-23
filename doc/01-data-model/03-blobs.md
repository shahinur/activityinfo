
# Blobs

Fields may take the value of a file, image, or other large binary or text object, but these blobs
are stored seperately from resources themselves in the blob store. 

# Creation

Any authenticated user may create a blob. The user defines a new blob by generating a CUID that will serve as 
the blob id. The blob may be uploaded to the server using this ID.

The user who has created the blob always has read access to the blob via `/service/blob/{blobId}`

# Sharing 

BlobIds can be used a form field values for Image fields, or general attachment fields. In this case, other users
can access the blob not via the blobstore, but the resource store:
 `/service/store/resource/{resourceId}/field/{fieldId}/blob/{blobId}`
 
# Clean up

Only blobs associated with resources will be retained. Blobs which are not associated with a resource version
will be garbage collected after a few months.