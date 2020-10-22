package uk.gov.justice.digital.needs.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.needs.api.CalculateNeedsDto
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto

@Service
class CriminogenicNeedsService {
    fun calculateNeeds(calculateNeedsDto: CalculateNeedsDto): CriminogenicNeedsDto {
        return CriminogenicNeedsDto()
    }

}
