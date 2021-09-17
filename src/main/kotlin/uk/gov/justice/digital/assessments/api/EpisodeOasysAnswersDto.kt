package uk.gov.justice.digital.assessments.api

data class EpisodeOasysAnswersDto(val episodeAnswers: List<EpisodeOasysAnswerDto> = listOf())

data class EpisodeOasysAnswerDto(
  val questionCode: String,
  val answer: String
)
