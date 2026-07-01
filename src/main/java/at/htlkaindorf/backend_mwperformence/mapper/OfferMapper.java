package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.OfferDTO;
import at.htlkaindorf.backend_mwperformence.entites.Offer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ServiceMapper.class})
public interface OfferMapper {

    @Mapping(source = "serviceEntities", target = "services")
    @Mapping(target = "icon", ignore = true)
    OfferDTO toDto(Offer offer);

    @Mapping(source = "services", target = "serviceEntities")
    @Mapping(target = "icon", ignore = true)
    Offer toEntity(OfferDTO offerDTO);

    List<OfferDTO> toDto(List<Offer> offers);
    List<Offer> toEntity(List<OfferDTO> offerDTOs);
}