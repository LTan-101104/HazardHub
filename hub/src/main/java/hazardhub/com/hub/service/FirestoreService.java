package hazardhub.com.hub.service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class FirestoreService {

    private final Firestore firestore;

    public <T> void setDocument(String collection, String documentId, T data)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collection).document(documentId);
        docRef.set(data).get();
        log.debug("Document {} set in collection {}", documentId, collection);
    }

    public <T> T getDocument(String collection, String documentId, Class<T> clazz)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collection).document(documentId);
        DocumentSnapshot snapshot = docRef.get().get();

        if (snapshot.exists()) {
            return snapshot.toObject(clazz);
        }
        return null;
    }

    public void deleteDocument(String collection, String documentId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collection).document(documentId);
        docRef.delete().get();
        log.debug("Document {} deleted from collection {}", documentId, collection);
    }
}
