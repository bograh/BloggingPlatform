# Database Schema Diagram

## Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SMART BLOGGING PLATFORM                              │
│                    Hybrid SQL/NoSQL Database Schema                          │
└─────────────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════
                           POSTGRESQL TABLES (SQL)
═══════════════════════════════════════════════════════════════════════════════

┌──────────────────────┐
│       USERS          │
├──────────────────────┤
│ PK user_id (SERIAL)  │
│ ✱  username (UNIQUE) │
│ ✱  email (UNIQUE)    │
│    password_hash     │
│    role (ENUM)       │
│    bio               │
│    created_at        │
│    updated_at        │
└──────────┬───────────┘
           │
           │ 1:N (Author)
           │
           ▼
┌──────────────────────┐
│       POSTS          │
├──────────────────────┤
│ PK post_id (SERIAL)  │
│ ✱  title [INDEXED]   │◄─────────────┐
│    content (TEXT)    │              │
│ FK author_id         │──┐           │
│    created_at        │  │           │
│    updated_at        │  │           │
│    status (ENUM)     │  │           │
│    view_count        │  │           │
└──────────┬───────────┘  │           │
           │              │           │
           │ 1:N          │           │ N:M
           │              │           │
           ▼              │           │
┌──────────────────────┐  │     ┌─────┴──────────┐
│  COMMENTS (Meta)     │  │     │   POST_TAGS    │
├──────────────────────┤  │     ├────────────────┤
│ PK comment_id        │  │     │ FK post_id     │
│ FK post_id [INDEXED] │  │     │ FK tag_id      │
│ FK user_id           │──┘     │ PK (post_id,   │
│    parent_comment_id │        │     tag_id)    │
│    mongo_document_id │        │ assigned_at    │
│    created_at        │        └────────┬───────┘
│    is_deleted        │                 │
└──────────────────────┘                 │
           │                             │
           │ References                  │ N:M
           │                             │
           ▼                             ▼
┌──────────────────────┐        ┌────────────────┐
│  MONGODB COLLECTION  │        │     TAGS       │
│   (comments)         │        ├────────────────┤
├──────────────────────┤        │ PK tag_id      │
│ _id (ObjectId)       │        │ ✱  tag_name    │
│ comment_id           │        │    [INDEXED]   │
│ post_id              │        │    description │
│ user_id              │        │    created_at  │
│ username             │        │    usage_count │
│ content              │        └────────────────┘
│ parent_comment_id    │
│ replies []           │
│ likes_count          │
│ created_at           │
│ updated_at           │
└──────────────────────┘

           ┌──────────────────────┐
           │      REVIEWS         │
           ├──────────────────────┤
           │ PK review_id (SERIAL)│
           │ FK post_id [INDEXED] │────┐
           │ FK user_id           │──┐ │
           │    rating (1-5)      │  │ │
           │    review_text       │  │ │
           │    created_at        │  │ │
           │    updated_at        │  │ │
           └──────────────────────┘  │ │
                                     │ │
                 ┌───────────────────┘ │
                 │                     │
                 │                     │
         References to                 │
         USERS                    References to
                                      POSTS

═══════════════════════════════════════════════════════════════════════════════
                            RELATIONSHIPS SUMMARY
═══════════════════════════════════════════════════════════════════════════════

Users ──(1:N)──> Posts            (One user writes many posts)
Users ──(1:N)──> Comments         (One user writes many comments)
Users ──(1:N)──> Reviews          (One user writes many reviews)

Posts ──(1:N)──> Comments         (One post has many comments)
Posts ──(1:N)──> Reviews          (One post has many reviews)
Posts ──(N:M)──> Tags             (Many posts have many tags)

Comments ──(1:N)──> Comments      (Self-referencing: parent-child replies)
Comments ──(1:1)──> MongoDB Doc   (Hybrid: metadata in SQL, content in NoSQL)

═══════════════════════════════════════════════════════════════════════════════
                               INDEXES & KEYS
═══════════════════════════════════════════════════════════════════════════════

PRIMARY KEYS:
  • users.user_id
  • posts.post_id
  • comments.comment_id
  • tags.tag_id
  • reviews.review_id
  • post_tags(post_id, tag_id) - Composite

FOREIGN KEYS:
  • posts.author_id → users.user_id
  • comments.post_id → posts.post_id
  • comments.user_id → users.user_id
  • comments.parent_comment_id → comments.comment_id
  • reviews.post_id → posts.post_id
  • reviews.user_id → users.user_id
  • post_tags.post_id → posts.post_id
  • post_tags.tag_id → tags.tag_id

B-TREE INDEXES (Performance Optimization):
  ✱ users.username         - Unique login lookup
  ✱ users.email            - Unique email verification
  ✱ posts.title            - Text search queries
  ✱ posts.author_id        - Filter posts by author
  ✱ comments.post_id       - Fetch all comments for a post
  ✱ tags.tag_name          - Tag-based search
  ✱ reviews.post_id        - Aggregate ratings per post

MONGODB INDEXES:
  • comments.comment_id    - Link to SQL metadata
  • comments.post_id       - Query comments by post
  • comments.created_at    - Sort by date

═══════════════════════════════════════════════════════════════════════════════
                        DATA TYPES & CONSTRAINTS
═══════════════════════════════════════════════════════════════════════════════

USERS TABLE:
  user_id        SERIAL PRIMARY KEY
  username       VARCHAR(50) UNIQUE NOT NULL
  email          VARCHAR(100) UNIQUE NOT NULL
  password_hash  VARCHAR(255) NOT NULL
  role           user_role_enum NOT NULL DEFAULT 'READER'
  bio            TEXT
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP

POSTS TABLE:
  post_id        SERIAL PRIMARY KEY
  title          VARCHAR(200) NOT NULL
  content        TEXT NOT NULL
  author_id      INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  status         post_status_enum DEFAULT 'DRAFT'
  view_count     INTEGER DEFAULT 0

COMMENTS TABLE (PostgreSQL Metadata):
  comment_id           SERIAL PRIMARY KEY
  post_id              INTEGER NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE
  user_id              INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
  parent_comment_id    INTEGER REFERENCES comments(comment_id) ON DELETE CASCADE
  mongo_document_id    VARCHAR(24) NOT NULL
  created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  is_deleted           BOOLEAN DEFAULT FALSE

TAGS TABLE:
  tag_id         SERIAL PRIMARY KEY
  tag_name       VARCHAR(50) UNIQUE NOT NULL
  description    TEXT
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  usage_count    INTEGER DEFAULT 0

POST_TAGS TABLE:
  post_id        INTEGER REFERENCES posts(post_id) ON DELETE CASCADE
  tag_id         INTEGER REFERENCES tags(tag_id) ON DELETE CASCADE
  assigned_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  PRIMARY KEY (post_id, tag_id)

REVIEWS TABLE:
  review_id      SERIAL PRIMARY KEY
  post_id        INTEGER NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE
  user_id        INTEGER NOT NULL REFERENCES users(user_id) ON DELETE CASCADE
  rating         INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5)
  review_text    TEXT
  created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
  UNIQUE(post_id, user_id)  -- One review per user per post

ENUMS:
  user_role_enum:   'ADMIN', 'AUTHOR', 'READER'
  post_status_enum: 'DRAFT', 'PUBLISHED', 'ARCHIVED'

═══════════════════════════════════════════════════════════════════════════════
                    HYBRID STORAGE ARCHITECTURE
═══════════════════════════════════════════════════════════════════════════════

┌───────────────────────────────────────────────────────────────────────┐
│                    COMMENT STORAGE STRATEGY                           │
├───────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  PostgreSQL (Relational)              MongoDB (Document Store)       │
│  ┌──────────────────────┐              ┌─────────────────────────┐  │
│  │ Comments Metadata    │              │ Comment Full Content    │  │
│  ├──────────────────────┤              ├─────────────────────────┤  │
│  │ • IDs & References   │◄────────────►│ • Complete text content │  │
│  │ • Relationships      │   Link via   │ • Nested reply structure│  │
│  │ • Timestamps         │  comment_id  │ • Flexible schema       │  │
│  │ • Foreign Keys       │              │ • Rich metadata         │  │
│  │ • Delete flags       │              │ • Fast read/write       │  │
│  └──────────────────────┘              └─────────────────────────┘  │
│                                                                       │
│  Benefits:                                                            │
│  ✓ Referential integrity (SQL)                                       │
│  ✓ Flexible nested comments (NoSQL)                                  │
│  ✓ Fast hierarchical queries (MongoDB)                               │
│  ✓ ACID compliance for critical data (PostgreSQL)                    │
│  ✓ Scalable document storage (MongoDB)                               │
└───────────────────────────────────────────────────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════
                        NORMALIZATION (3NF)
═══════════════════════════════════════════════════════════════════════════════

1st Normal Form (1NF):
  ✓ All tables have primary keys
  ✓ All columns contain atomic values
  ✓ No repeating groups

2nd Normal Form (2NF):
  ✓ Meets 1NF requirements
  ✓ No partial dependencies on composite keys
  ✓ post_tags uses composite key properly

3rd Normal Form (3NF):
  ✓ Meets 2NF requirements
  ✓ No transitive dependencies
  ✓ author_name not stored in posts (retrieved via FK)
  ✓ tag_name not duplicated in post_tags
  ✓ Separate tags table eliminates redundancy

═══════════════════════════════════════════════════════════════════════════════
                        QUERY PATTERNS
═══════════════════════════════════════════════════════════════════════════════

Common Query Examples:

1. Get all posts by an author with their tags:
   SELECT p.*, u.username, array_agg(t.tag_name) as tags
   FROM posts p
   JOIN users u ON p.author_id = u.user_id
   LEFT JOIN post_tags pt ON p.post_id = pt.post_id
   LEFT JOIN tags t ON pt.tag_id = t.tag_id
   WHERE u.user_id = ?
   GROUP BY p.post_id, u.username

2. Get post with comments (hybrid query):
   SQL: SELECT * FROM comments WHERE post_id = ?
   MongoDB: db.comments.find({ post_id: ? })

3. Get top-rated posts:
   SELECT p.*, AVG(r.rating) as avg_rating, COUNT(r.review_id) as review_count
   FROM posts p
   LEFT JOIN reviews r ON p.post_id = r.post_id
   GROUP BY p.post_id
   ORDER BY avg_rating DESC, review_count DESC
   LIMIT 10

4. Search posts by tag:
   SELECT p.* FROM posts p
   JOIN post_tags pt ON p.post_id = pt.post_id
   JOIN tags t ON pt.tag_id = t.tag_id
   WHERE t.tag_name = ?

5. Get threaded comments from MongoDB:
   db.comments.find({ post_id: ?, parent_comment_id: null })
     .sort({ created_at: -1 })

═══════════════════════════════════════════════════════════════════════════════

Legend:
  PK  = Primary Key
  FK  = Foreign Key
  ✱   = Indexed Column
  1:N = One-to-Many Relationship
  N:M = Many-to-Many Relationship
  ──> = References/Points to
