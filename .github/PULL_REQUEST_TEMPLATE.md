# Pull Request Checklist

## General

- [ ] Did you use the Jira Issue number in the commit comments?
- [ ] Did you set meaningful commit comments on each commit?
- [ ] Did you sign off all commits for this PR? (git commit -s -m "jira issue number - your commit comment")

## Function

- [ ] Does the project still build fine locally?
- [ ] Does your modification work?
- [ ] Is the non-happy path working, too?
- [ ] Are other parts that use the same component still working fine?

## Code Style

- [ ] Are method-/class-/variable-names meaningful?
- [ ] Are methods concise, not too long?
- [ ] Are catch blocks catching precise Exceptions only (no catch all)?
- [ ] Have you used the correct file header copyright comment?
- [ ] Have you set the correct year in the headers of new files?

## Tests

- [ ] Are there unit-tests?
- [ ] Are there integration tests (or at least a jira to tackle these)?
