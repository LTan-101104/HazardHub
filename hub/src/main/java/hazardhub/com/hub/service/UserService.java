package hazardhub.com.hub.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import hazardhub.com.hub.exception.BadRequestException;
import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.model.dto.UserRegistration;
import hazardhub.com.hub.model.dto.UserResponse;
import hazardhub.com.hub.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class UserService {

    private static final String COLLECTION_NAME = "users";
    private final FirestoreService firestoreService;

    public UserResponse registerUser(UserRegistration request) throws ExecutionException, InterruptedException {
        log.info("Registering new user with email: {}", request.getEmail());

        try {
            // Create user in Firebase Authentication
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getDisplayName());

            // Only set phone if provided and properly formatted (E.164 format: +1234567890)
            if (request.getPhone() != null && !request.getPhone().isBlank()) {
                String phone = request.getPhone();
                if (!phone.startsWith("+")) {
                    phone = "+" + phone;
                }
                createRequest.setPhoneNumber(phone);
            }

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            // Create user document in Firestore
            User user = User.builder()
                    .email(request.getEmail())
                    .displayName(request.getDisplayName())
                    .phone(request.getPhone())
                    .build();

            firestoreService.setDocument(COLLECTION_NAME, userRecord.getUid(), user);
            user.setId(userRecord.getUid());

            log.info("User registered successfully with UID: {}", userRecord.getUid());
            return mapToResponseDTO(user);

        } catch (FirebaseAuthException e) {
            log.error("Firebase authentication error: {}", e.getMessage());
            throw new BadRequestException("Failed to create user: " + e.getMessage());
        }
    }

    public UserResponse getUserById(String uid) throws ExecutionException, InterruptedException {
        log.info("Fetching user with UID: {}", uid);

        User user = firestoreService.getDocument(COLLECTION_NAME, uid, User.class);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + uid);
        }

        user.setId(uid);
        return mapToResponseDTO(user);
    }

    public UserResponse updateUser(String uid, UserResponse updates) throws ExecutionException, InterruptedException {
        log.info("Updating user: {}", uid);

        User user = firestoreService.getDocument(COLLECTION_NAME, uid, User.class);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + uid);
        }

        if (updates.getDisplayName() != null) {
            user.setDisplayName(updates.getDisplayName());
        }
        if (updates.getPhone() != null) {
            user.setPhone(updates.getPhone());
        }
        if (updates.getInsuranceDispatchConfig() != null) {
            user.setInsuranceDispatchConfig(updates.getInsuranceDispatchConfig());
        }

        firestoreService.setDocument(COLLECTION_NAME, uid, user);
        user.setId(uid);

        return mapToResponseDTO(user);
    }

    /**
     * Generate a custom token for a user by email.
     * The client can exchange this for an ID token using Firebase REST API.
     */
    public String createCustomToken(String email) {
        // this method assumes the user already exists in firebase authentication
        // only used in dev / testing environments
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
            String customToken = FirebaseAuth.getInstance().createCustomToken(userRecord.getUid());
            log.info("Created custom token for user: {}", email);
            return customToken;
        } catch (FirebaseAuthException e) {
            log.error("Failed to create custom token: {}", e.getMessage());
            throw new BadRequestException("Invalid credentials or user not found");
        }
    }

    private UserResponse mapToResponseDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .insuranceDispatchConfig(user.getInsuranceDispatchConfig())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
