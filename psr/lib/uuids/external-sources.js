const { DataFile } = require('../data-files')

const externalsCsv = 'external-sources.csv'

function loadExternals() {
  return DataFile.load(externalsCsv)
}

const externals = loadExternals()

module.exports = ref => externals[ref]

