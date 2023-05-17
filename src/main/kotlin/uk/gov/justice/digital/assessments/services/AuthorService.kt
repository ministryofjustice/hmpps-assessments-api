package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AuthorRepository
import uk.gov.justice.digital.assessments.utils.RequestData

@Service
class AuthorService(
  private val authorRepository: AuthorRepository,
) {

  fun getOrCreateAuthor(): AuthorEntity {
    val author = RequestData.getUserAuthSource()?.let {
      authorRepository.findByUserIdAndUserAuthSource(
        RequestData.getUserId(),
        it,
      )
    }

    if (author == null) {
      val newAuthor = AuthorEntity(
        userId = RequestData.getUserId(),
        userName = RequestData.getUserName(),
        userAuthSource = RequestData.getUserAuthSource(),
        userFullName = RequestData.getUserFullName(),
      )
      return authorRepository.save(newAuthor)
    }
    return author
  }
}
