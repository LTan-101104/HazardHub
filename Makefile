# Google Cloud Deploy
REGION = us-central1
SERVICE = hazardhub
CORS_ORIGINS = https://hazard-hub-pymz.vercel.app

deploy:
	cd hub && gcloud run deploy $(SERVICE) \
		--source . \
		--region $(REGION) \
		--allow-unauthenticated \
		--clear-base-image \
		--set-secrets "SPRING_DATA_MONGODB_URI=SPRING_DATA_MONGODB_URI:latest,GEMINI_API_KEY=GEMINI_API_KEY:latest,GOOGLE_MAPS_API_KEY=GOOGLE_MAPS_API_KEY:latest" \
		--set-env-vars "CORS_ALLOWED_ORIGINS=$(CORS_ORIGINS)"

logs:
	gcloud run services logs read $(SERVICE) --region $(REGION) --limit 100

secret-view:
	gcloud secrets versions access latest --secret=SPRING_DATA_MONGODB_URI

# Local Docker
up:
	docker compose up --build

down:
	docker compose down
