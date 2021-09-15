
# Targets
fmt:  ## Formats and fixes code with Scalafix and Scalafmt
	sbt fmt

test:  ## Test library across all Scala versions
	sbt +test

release: test  ## Release library across all Scala versions to Maven Central
	sbt +publishSigned
	sbt sonatypeBundleRelease

clean:
	sbt clean
	rm -rf .bloop .bsp .metals

help:
	@echo "Makefile targets:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = "[:##]"}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$4}'
	@echo ""

.PHONY: test fmt clean help
.DEFAULT_GOAL := help
