package in.moosaviqar.cloudshareapi.repository;

import in.moosaviqar.cloudshareapi.document.FileMetaDataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FileMetaDataRepository extends MongoRepository<FileMetaDataDocument, String >{
    List<FileMetaDataDocument> findByClerkId(String clerkId);

    long countByClerkId(String clerkId);

}
