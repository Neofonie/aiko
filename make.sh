#!/bin/bash
set -Eeuo pipefail
IFS=''

# Init environment
init () {
	echo Building aiko-app
	image_name="aiko"
	local_image_name=$image_name':local'
    # TODO: change to public repo
	remote_image_name='delivery.wub:5000/'$image_name':'${GO_PIPELINE_COUNTER:-local}
	latest_image_name='delivery.wub:5000/'$image_name':latest'
	trap "clean" EXIT
}

fail () {
	echo Failed:
	echo $*
	exit 1
}

# Clean up environment
clean () {
	echo "Cleaning up ..."
	[ -z "${cid:-}" ] || docker rm -v $cid
	echo "Cleaned!"
}

# Compile the java artifact
compile () {
	echo "Building java artifact"
	docker pull delivery.wub:5000/maven:latest
	cid=`docker create -v /work delivery.wub:5000/maven:latest`
	docker cp java/. $cid:/work
	docker run -i --rm --volumes-from $cid --workdir /work delivery.wub:5000/maven:latest mvn --batch-mode clean install
	docker cp $cid:/work/target/application.jar docker/payloads
}


# Create docker image
build () {
	echo "Building Docker image"
	docker build --no-cache --pull=true -f docker/Dockerfile --force-rm=true -t $local_image_name docker
}

# Release the local image to the repo
release() {
	echo Releasing local Docker image
	echo Moving $local_image_name to $remote_image_name
	docker tag -f $local_image_name $remote_image_name
	docker push $remote_image_name

	echo Moving $local_image_name to $latest_image_name
	docker tag -f $local_image_name $latest_image_name
	docker push $latest_image_name
}

all () {
	compile
	build
}


main () {
	init
	${1:-all}
	echo Success
}

main "$@"
