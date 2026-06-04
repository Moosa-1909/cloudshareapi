package in.moosaviqar.cloudshareapi.repository;

import in.moosaviqar.cloudshareapi.document.ProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<ProfileDocument, String>{

    Optional<ProfileDocument> findByEmail(String email);
}
