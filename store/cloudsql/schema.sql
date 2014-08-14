
-- Reset

DROP TABLE IF EXISTS global_version;
DROP TABLE IF EXISTS resource;
DROP TABLE IF EXISTS resource_version;
DROP TABLE IF EXISTS pending_commits;


-- Holds the current version of the database
CREATE TABLE global_version (current_version BIGINT DEFAULT 1);
INSERT INTO global_version (current_version) VALUES (1);


-- Holds the latest versions of resources
CREATE TABLE resource (
  id VARCHAR(64) BINARY PRIMARY KEY,      -- ResourceId
  version BIGINT NOT NULL,                -- The Resource's version
  owner_id VARCHAR(64) BINARY NOT NULL,   -- The Resource that owns this resource
  class_id VARCHAR(64) BINARY,            -- The FormClassId of this resource
  label VARCHAR(255),                     -- For FormClasses and Folders, the label
  sub_tree_version BIGINT,                -- The most recent version of this resource or any resource directly or transitively
                                          -- owned by this resource
  next_sequence BIGINT,                   -- The next sequence number (for FormClasses only)
  sequence BIGINT,                        -- This FormInstance's sequence number
  content LONGTEXT                           -- The Resource encoded as Message Pack
);

-- Holds a copy of all revisions
CREATE TABLE resource_version (
  id VARCHAR(64) BINARY,                          -- The ResourceId of this resource
  version BIGINT NOT NULL,                        -- The global version of this resource
  owner_id VARCHAR(64) BINARY,                    -- The resource which owned this resource at this version
  user_id VARCHAR(64) BINARY,                     -- The user who created the version
  changeset_id VARCHAR(64) BINARY,                -- The logical changeset of which this is a member
  commit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,   -- The clock time of the commit on the server
  creation_time TIMESTAMP,                        -- The clock time at which the user created the version
                                                  -- (reported by client)
  content LONGTEXT,                               -- Encoded contents as MessagePackage
  PRIMARY KEY (id, version));

-- Pending commits
-- These are commits that could not be applied immediately due
-- to congestion and are queued
CREATE TABLE pending_commits (
  user_id VARCHAR(64) NOT NULL,
  submit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  content LONGTEXT);


-- Holds a list, for each user, of "root" resources that have been shared with the user
-- In this case, a "root" resource is a resource that they have access to, but not to its parent
CREATE TABLE user_root_index (
  user_id VARCHAR(64) NOT NULL,
  resource_id VARCHAR(64) NOT NULL,
  PRIMARY KEY (user_id, resource_id)
);