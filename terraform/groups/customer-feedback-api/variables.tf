variable "environment" {
  description = "The environment name to use within Concourse"
  type        = string
}

variable "repository_name" {
  default     = "customer-feedback-api"
  description = "The name of the repository in which we're operating"
  type        = string
}

variable "region" {
  description = "The AWS region in which resources will be administered"
  type        = string
}

variable "service" {
  default     = "customer-feedback-api"
  description = "The service name to be used when creating AWS resources"
  type        = string
}
