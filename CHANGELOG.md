# Changelog

## [0.3.5] - 2019-10-02
### Changed
- Removed bug on balance screen that showed up when all bills were settled up.

## [0.3.4] - 2019-10-02
### Changed
- Removed unnecessary strings in german translation.
- Improved layouts.

## [0.3.3] - 2019-09-29
### Added
- Flag to disable share group dialog.
- Menu in BillDetailActivity to remove bill.

### Changed
- Improved Layout

## [0.3.2] - 2019-09-24
### Changed
- Fixed help text that shows amount to balance and percentage to balance

## [0.3.1] - 2019-09-23
### Changed
- Do not count bills that are removed in balances screen total spent.

## [0.3.0] - 2019-09-22
### Added
- Show what group and each member total spent in balances screen.
- Allow adding member to group after group was created.

### Changed
- It is now possible to mix percent and absolute in custom split

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
