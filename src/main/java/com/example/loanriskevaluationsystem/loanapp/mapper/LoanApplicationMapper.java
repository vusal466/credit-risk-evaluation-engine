package com.example.loanriskevaluationsystem.loanapp.mapper;

import com.example.loanriskevaluationsystem.loanapp.dto.request.LoanApplicationRequest;
import com.example.loanriskevaluationsystem.loanapp.entity.LoanApplication;
import org.mapstruct.*;



@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanApplicationMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "applicationDate", expression = "java(java.time.LocalDateTime.now())")
        @Mapping(target = "status", constant = "PENDING")
        @Mapping(target = "riskEvaluation", ignore = true)
        @Mapping(target = "updatedAt", ignore = true)
        LoanApplication toEntity(LoanApplicationRequest request);

        @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "applicationDate", ignore = true)
        @Mapping(target = "status", ignore = true)
        @Mapping(target = "riskEvaluation", ignore = true)
        @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
        void updateEntity(@MappingTarget LoanApplication entity, LoanApplicationRequest request);

}
