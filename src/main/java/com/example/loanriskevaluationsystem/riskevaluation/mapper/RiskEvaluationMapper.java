package com.example.loanriskevaluationsystem.riskevaluation.mapper;


import com.example.loanriskevaluationsystem.riskevaluation.dto.request.RiskEvaluationRequest;
import com.example.loanriskevaluationsystem.riskevaluation.dto.response.RiskEvaluationResponse;
import com.example.loanriskevaluationsystem.riskevaluation.entity.RiskEvaluation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RiskEvaluationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "evaluatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "loanApplication", ignore = true)
    RiskEvaluation toEntity(RiskEvaluationRequest request);

    @Mapping(target = "loanApplicationId",
            expression = "java(entity.getLoanApplication() != null ? entity.getLoanApplication().getId() : null)")
    @Mapping(target = "customerId",
            expression = "java(entity.getLoanApplication() != null ? entity.getLoanApplication().getCustomerId() : null)")
    @Mapping(target = "requestedAmount",
            expression = "java(entity.getLoanApplication() != null ? entity.getLoanApplication().getRequestedAmount() : null)")
    RiskEvaluationResponse toResponse(RiskEvaluation entity);

    List<RiskEvaluationResponse> toResponseList(List<RiskEvaluation> entities);

}