package tracker.authz

import rego.v1

default allow := false

allow if {
    input.subject.id != ""
    input.subject.tenant_id != ""
    input.subject.roles[_] == "USER"

    input.action == "READ"

    input.resource.type == "TRANSACTION"
    input.resource.id != ""
    input.resource.tenant_id != ""

    input.subject.tenant_id == input.resource.tenant_id
}