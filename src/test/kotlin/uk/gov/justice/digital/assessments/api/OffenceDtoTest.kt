package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.MainOffence
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Sentence
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Type
import java.time.LocalDate

@DisplayName("Offence DTO Tests")
class OffenceDtoTest {

  @Test
  fun `builds offence DTO from Sentence from Delius`() {
    val sentence = Sentence(
      startDate = LocalDate.of(2020, 2, 1),
      mainOffence = MainOffence(
        category = Type(
          code = "main category code",
          description = "code description 1",
        ),
        subCategory = Type(
          code = "subcategory code",
          description = "code description 2",
        ),
      ),
    )

    val offenceDto = OffenceDto.from(sentence, 1)

    assertThat(offenceDto?.eventId).isEqualTo(1)
    assertThat(offenceDto?.sentenceDate).isEqualTo(LocalDate.of(2020, 2, 1))
    assertThat(offenceDto?.offenceCode).isEqualTo("main category code")
    assertThat(offenceDto?.codeDescription).isEqualTo("code description 1")
    assertThat(offenceDto?.offenceSubCode).isEqualTo("subcategory code")
    assertThat(offenceDto?.subCodeDescription).isEqualTo("code description 2")
  }
}
