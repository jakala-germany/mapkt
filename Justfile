# MapKt - KSP Code Mapping Generator for Kotlin Multiplatform
# This Justfile provides convenient commands for development and publishing

default:
  just --list

# ============== BUILD & DEVELOPMENT TASKS ==============

# Full build - compiles all modules and verifies sample project runs
build: check-sample clean
	@echo "✅ Build complete!"

# Run code formatting checks
format:
    @./gradlew ktlintFormat

# Clean build artifacts without removing local repo/cache
clean:
	@./gradlew clean

# Run tests (if configured)
test:
    @./gradlew sample:jvmTest

# Check sample project compiles correctly (requires KSP to run first)
check-sample: kspCommonMainKotlinMetadata
	@./gradlew :sample:build

# Run KSP metadata generation for commonMain (required before building sample)
kspCommonMainKotlinMetadata:
	@./gradlew kspCommonMainKotlinMetadata

# ============== CLEAN-UP COMBINED TASKS ==============

# Clean build artifacts and check samples in one go (useful for CI)
clean-check-sample: clean check-sample
	@echo "✅ Clean & sample verified"

# ============== PUBLISHING TASKS ==============

# Publish artifacts to local Maven repository
publish-local: clean
	@./gradlew publishToMavenLocal --info
	@echo "📦 Published to local maven repo"
