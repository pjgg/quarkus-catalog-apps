# Deployment

## Requirements

* Helm
* [helm-diff plugin](https://github.com/databus23/helm-diff)
* helmfile
    * How to install(Linux):
    ```
      wget https://github.com/roboll/helmfile/releases/download/v0.134.0/helmfile_linux_amd64 -O helmfile_linux_amd64
      chmod +x helmfile_linux_amd64
      sudo mv helmfile_linux_amd64 /usr/local/bin/helmfile
    ```
  * [oc](https://docs.openshift.com/enterprise/3.2/cli_reference/manage_cli_profiles.html#manually-configuring-cli-profiles) / kubectl Cli


## Helm

Move on to your helmfile environment, for example local and then apply your changes:
> cd /helmfiles/local
>
> helmfile -f helmfile.yaml apply/sync

## Crds 

Currently, there is [not possible](https://github.com/roboll/helmfile/issues/1353) to install custom resource definition objects by a helmfile. So in order to install postgresql and kafka cluster we are running the following command:

 > oc create -f yourResourcedefinition.yaml
 >

This project requires the following operators installed:
* [Postgresql Operator](https://operatorhub.io/operator/postgresql-operator-dev4devs-com)
* [Kafka Strimzi Operator](https://strimzi.io/)

### Folder Structure
```
.
├── charts
│   ├── catalog-enricher-service
│   │   ├── Chart.yaml
│   │   ├── templates
│   │   │   ├── deployment.yaml
│   │   │   ├── _helpers.tpl
│   │   │   └── service.yaml
│   │   └── values.yaml
│   ├── catalog-rest-api
│   │   ├── Chart.yaml
│   │   ├── templates
│   │   │   ├── deployment.yaml
│   │   │   ├── _helpers.tpl
│   │   │   ├── route.yaml
│   │   │   └── service.yaml
│   │   └── values.yaml
│   └── catalog-storage-service
│       ├── Chart.yaml
│       ├── templates
│       │   ├── deployment.yaml
│       │   ├── _helpers.tpl
│       │   └── service.yaml
│       └── values.yaml
├── crds
│   ├── dev
│   │   └── postgresql
│   │       └── quarkusappcatalog.yaml
│   ├── local
│   └── prod
├── docker
│   └── docker-compose.yaml
├── helmfiles
│   ├── dev
│   │   └── helmfile.yaml
│   ├── local
│   │   ├── helmfile.yaml
│   │   └── values.yaml
│   └── prod
├── README.md
└── values
    ├── dev
    │   ├── catalog-enricher-service
    │   │   └── values.yaml
    │   ├── catalog-rest-api
    │   │   └── values.yaml
    │   └── catalog-storage-service
    │       └── values.yaml
    └── replica-values.yaml

```
**Chart:** a collection of k8s templates and default values that applies to those templates. Doesn't talk about environment specific values.

**helmfiles:** Is a declarative spec for deploying helm charts. Here you have environment specific values. A helmfile could deploy more than one chart, 
and also DBs, brokers, secrets etc...everything that is required in order to make your service running must be defined in these helmfiles. 

**values:** these values overwrite default template values, and are environment specific.

**crds** custom resource definition object. According [helm folder structure definition](https://helm.sh/docs/topics/charts/) should be located at the same level of chart folder. However, due to an [issue](https://github.com/roboll/helmfile/issues/1353) we decided to move on this folder out of this structure.  
