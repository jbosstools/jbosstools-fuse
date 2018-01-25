# Pull Request Checklist

After this checklist is all checked or PR provides explanations for possible pass-through, please put the label "Ready for review" on the PR.


## General

- [ ] Did you use the Jira Issue number in the commit comments?
- [ ] Did you set meaningful commit comments on each commit?
- [ ] Did you sign off all commits for this PR? (git commit -s -m "jira issue number - your commit comment")

## Functional

- [ ] Did the CI job report a successful build?
- [ ] Is the non-happy path working, too?

## Maintainability

- [ ] Are all Sonar reported issues fixed or can they be ignored?
- [ ] Is there no duplicated code?
- [ ] Are method-/class-/variable-names meaningful?

## Tests

- [ ] Are there unit-tests?
- [ ] Are there integration tests (or at least a jira to tackle these)?
- [ ] Do we need a new UI test?

## Legal

- [ ] Have you used the correct file header copyright comment?
- [ ] Have you used the correct year in the headers of new files?

