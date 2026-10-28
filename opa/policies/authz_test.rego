package tracker.authz

import rego.v1

test_same_tenant_read_allowed if {
    allow with input as {
        "subject": {"id": "user-1", "tenant_id": "tenant-1", "roles": ["USER"]
        },
        "action": "READ",
        "resource": {"type": "TRANSACTION","id": "tx-1","tenant_id": "tenant-1"
        }
    }
}

test_cross_tenant_read_denied if {
    not allow with input as {
        "subject": {"id": "user-1", "tenant_id": "tenant-1", "roles": ["USER"]
        },
        "action": "READ",
        "resource": {"type": "TRANSACTION", "id": "tx-2", "tenant_id": "tenant-2"
        }
    }
}

test_missing_tenant_denied if {
    not allow with input as {
        "subject": {"id": "user-1", "roles": ["USER"]
        },
        "action": "READ",
        "resource": {"type": "TRANSACTION", "id": "tx-1", "tenant_id": "tenant-1"
        }
    }
}

test_unsupported_action_denied if {
    not allow with input as {
        "subject": {"id": "user-1", "tenant_id": "tenant-1", "roles": ["USER"]
        },
        "action": "DELETE",
        "resource": {"type": "TRANSACTION", "id": "tx-1", "tenant_id": "tenant-1"
        }
    }
}