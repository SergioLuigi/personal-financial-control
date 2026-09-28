---
paths:
  - "docs/**"
---

# Maintaining the docs

- The use case text is authoritative; the diagrams follow it. Whenever a flow
  in `docs/use-cases/` changes, update `docs/use-case-diagrams.md` in the same
  change.
- Keep `docs/README.md` in sync when use cases are added, removed or
  renumbered, and update its "Last updated" date.
- To render the diagrams from WSL, run `npx -y @mermaid-js/mermaid-cli@11` and
  pass Windows paths obtained with `wslpath -w`.
