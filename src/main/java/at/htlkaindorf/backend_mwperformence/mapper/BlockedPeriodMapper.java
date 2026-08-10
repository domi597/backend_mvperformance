package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.BlockedPeriodDTO;
import at.htlkaindorf.backend_mwperformence.entites.BlockedPeriod;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlockedPeriodMapper {
    BlockedPeriodDTO toDto(BlockedPeriod entity);
    BlockedPeriod toEntity(BlockedPeriodDTO dto);
    List<BlockedPeriodDTO> toDto(List<BlockedPeriod> entities);
}
