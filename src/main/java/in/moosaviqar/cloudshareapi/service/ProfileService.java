package in.moosaviqar.cloudshareapi.service;


import in.moosaviqar.cloudshareapi.document.ProfileDocument;
import in.moosaviqar.cloudshareapi.dto.ProfileDTO;
import in.moosaviqar.cloudshareapi.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    public ProfileDTO createProfile(ProfileDTO profileDTO){
                 ProfileDocument profile = ProfileDocument.builder()
                          .clerkId(profileDTO.getClerkId())
                          .email(profileDTO.getEmail())
                          .firstName(profileDTO.getFirstName())
                          .lastName(profileDTO.getLastName())
                          .photoUrl(profileDTO.getPhotoUrl())
                          .credits(5)
                          .createdAt(Instant.now())
                          .build();

                profile =  profileRepository.save(profile);

               return  ProfileDTO.builder()
                        .id(profile.getId())
                        .clerkId(profile.getClerkId())
                        .email(profile.getEmail())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .photoUrl(profile.getPhotoUrl())
                        .credits(profile.getCredits())
                        .createdAt(profile.getCreatedAt())
                        .build();

    }
}
