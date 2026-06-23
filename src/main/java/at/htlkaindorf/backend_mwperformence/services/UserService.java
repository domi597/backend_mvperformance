package at.htlkaindorf.backend_mwperformence.services;

import at.htlkaindorf.backend_mwperformence.dtos.UserDTO;
import at.htlkaindorf.backend_mwperformence.entites.User;
import at.htlkaindorf.backend_mwperformence.exception.ApiException;
import at.htlkaindorf.backend_mwperformence.mapper.UserMapper;
import at.htlkaindorf.backend_mwperformence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO getById(Long id) {
        return userMapper.toDTO(userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND)));
    }

    public List<UserDTO> getAll() {
        return userMapper.toDTOList(userRepository.findAll());
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getStreet() != null) user.setStreet(dto.getStreet());
        if (dto.getZip() != null) user.setZip(dto.getZip());
        if (dto.getCity() != null) user.setCity(dto.getCity());

        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ApiException("Benutzer nicht gefunden.", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}