FROM frolvlad/alpine-glibc

MAINTAINER Quarkus QE Team <quarkus-qe@redhat.com>

# Install prerequisites
RUN apk add --no-cache ca-certificates bash git curl openssl gettext

# Install Kubectl
RUN wget -q https://storage.googleapis.com/kubernetes-release/release/$(curl -sS https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl -O /usr/local/bin/kubectl \
    && chmod +x /usr/local/bin/kubectl

# Install helm
RUN curl -fsSL -o get_helm.sh https://raw.githubusercontent.com/helm/helm/master/scripts/get-helm-3 \
    && chmod +x get_helm.sh && ./get_helm.sh

# Install OC
ENV OC_VERSION=4.5
RUN curl -sLo /tmp/oc.tar.gz https://mirror.openshift.com/pub/openshift-v$(echo $OC_VERSION | cut -d'.' -f 1)/clients/oc/$OC_VERSION/linux/oc.tar.gz \
    && tar xzvf /tmp/oc.tar.gz -C /usr/local/bin/ \
    && rm -rf /tmp/oc.tar.gz

# Install Helmfile
RUN set -x \
    && wget https://github.com/roboll/helmfile/releases/download/v0.134.0/helmfile_linux_amd64 -O helmfile_linux_amd64 \
    && chmod +x helmfile_linux_amd64 \
    && mv helmfile_linux_amd64 /usr/local/bin/helmfile

# Install Helm-diff
RUN mkdir -p ~/.helm/plugins \ 
    && helm plugin install https://github.com/databus23/helm-diff

# Scripts
COPY ./promote.sh /
RUN chmod +x /promote.sh
ENTRYPOINT ["/promote.sh"]
