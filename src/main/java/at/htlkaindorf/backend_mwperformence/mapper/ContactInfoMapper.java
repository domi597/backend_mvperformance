package at.htlkaindorf.backend_mwperformence.mapper;

import at.htlkaindorf.backend_mwperformence.dtos.ContactInfoDTO;
import at.htlkaindorf.backend_mwperformence.entites.ContactInfo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Project: backend_MWPerformence
 * Created by: Dominik Ranegger
 * Date: 25.03.2026
 * Time: 12:54
 */
@Mapper(componentModel = "spring")
public interface ContactInfoMapper {

    ContactInfo toEntity(ContactInfoDTO contactInfoDTO);

    List<ContactInfo> toEntity(List<ContactInfoDTO> contactInfoDTO);

    ContactInfoDTO toDto(ContactInfo contactInfo);

    List<ContactInfoDTO> toDto(List<ContactInfo> contactInfo);
}
