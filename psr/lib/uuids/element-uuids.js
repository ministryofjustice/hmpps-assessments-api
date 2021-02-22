const { v4: uuid } = require('uuid')
const { UuidFile } = require('../data-files')

class ElementUuids {
  constructor(csvFile) {
    this.csvFile = csvFile
    this.uuids = UuidFile.load(this.csvFile)
  }

  lookup(ref, title) {
    if (!this.uuids[ref]) {
      this.uuids[ref] = uuid()
      UuidFile.append(this.csvFile, title, ref, this.uuids[ref])
    }
    return this.uuids[ref]
  }
}

module.exports = csvFile => new ElementUuids(csvFile)
