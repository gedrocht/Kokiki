# Wiki Software Deployment

This repository includes a separately deployable Wiki.js stack so the team can
maintain a hand-curated beginner wiki in addition to the generated docs site.

## Why both a docs site and a wiki?

- The docs site is versioned, reviewed, and ideal for durable reference.
- The wiki is conversational, editable, and ideal for onboarding notes.

## Deployment summary

Use the files in `wiki/` to launch Wiki.js and PostgreSQL with Docker Compose.
The `wiki/pages/` folder contains starter content that can be copied into the
live wiki after first boot.
