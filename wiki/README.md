# Wiki.js Deployment

This folder contains a starter deployment for a beginner-friendly internal wiki.

## Stack

- Wiki.js
- PostgreSQL

## Start the wiki

```bash
docker compose up -d
```

After the containers start, open `http://localhost:3000`, complete the initial
Wiki.js setup flow, and import the pages stored in `wiki/pages/`.
