#!/bin/bash

# List of images to test
images=(
  "quay.io/quarkus/quarkus-micro-image:3.8.4"
  "quay.io/quarkus/quarkus-micro-image:3.8"
  "quay.io/quarkus/quarkus-micro-image:2.2.0.Final"
  "quay.io/quarkus/quarkus-micro-image:2.2"
  "quay.io/quarkus/ubi9-quarkus-micro-image:2.0"
  "registry.access.redhat.com/ubi8/ubi-minimal:8.7"
  "scratch"
)

echo "Testing image availability..."

for image in "${images[@]}"; do
  echo -n "Pulling $image... "
  if docker pull "$image" &> /dev/null; then
    echo "✅ Available"
  else
    echo "❌ Not found"
  fi
done
