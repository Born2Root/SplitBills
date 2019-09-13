# Changelog

## [0.2.8] - 2019-09-13
### Added
- Custom percentual split feature

### Changed
- Made sure it is not possible to create a group with a group name that 
  already exists. 

## [0.2.7] - 2019-08-27
### Added
- Dialog for sharing group if new bill was added.

### Changed
- Fixed translations.
- A bill with an amount of zero is not saveable anymore.
- CHANGELOG renamed to CHANGELOG.md

## [0.2.6] - 2019-08-26
### Added
- Custom split (absolute) feature

## [0.2.5] - 2019-08-25
### Changed
- Made sure that it is not possible anymore to make empty description, amounts
  and names.
- Moved drawables from drawable-v24 to drawable directory.
- Changed context.getColor calls to to ContextComapt.getColor calls to prevent
  an api issue.
