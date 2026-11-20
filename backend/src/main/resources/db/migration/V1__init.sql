CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email TEXT NOT NULL, password_hash TEXT NOT NULL, full_name TEXT NOT NULL,
  avatar_key TEXT, email_verified BOOLEAN NOT NULL DEFAULT FALSE, enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE UNIQUE INDEX ux_users_email ON users (lower(email));
CREATE TABLE email_token (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  type VARCHAR(10) NOT NULL CHECK (type IN ('VERIFY','RESET')), token_hash TEXT NOT NULL UNIQUE,
  expires_at TIMESTAMPTZ NOT NULL, used_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE refresh_token (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  token_hash TEXT NOT NULL UNIQUE, family_id UUID NOT NULL, expires_at TIMESTAMPTZ NOT NULL,
  revoked_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE INDEX ix_refresh_family ON refresh_token (family_id);
CREATE TABLE organization (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), name TEXT NOT NULL, slug TEXT NOT NULL UNIQUE,
  created_by UUID REFERENCES users(id), deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE organization_member (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN','PROJECT_MANAGER','TEAM_LEAD','MEMBER')),
  status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE', created_at TIMESTAMPTZ NOT NULL DEFAULT now(), UNIQUE (org_id, user_id));
CREATE TABLE invitation (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  email TEXT NOT NULL, role VARCHAR(20) NOT NULL, token_hash TEXT NOT NULL UNIQUE,
  expires_at TIMESTAMPTZ NOT NULL, accepted_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE team (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  name TEXT NOT NULL, description TEXT, lead_id UUID REFERENCES users(id), deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE team_member (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), team_id UUID NOT NULL REFERENCES team(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE, UNIQUE (team_id, user_id));
CREATE TABLE project (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  name TEXT NOT NULL, description TEXT,
  status VARCHAR(12) NOT NULL DEFAULT 'PLANNING' CHECK (status IN ('PLANNING','ACTIVE','ON_HOLD','COMPLETED','CANCELLED')),
  start_date DATE, due_date DATE, owner_id UUID REFERENCES users(id), archived_at TIMESTAMPTZ, deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE INDEX ix_project_org ON project (org_id, status);
CREATE TABLE project_member (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE,
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE, project_role VARCHAR(20), UNIQUE (project_id, user_id));
CREATE TABLE task (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  project_id UUID NOT NULL REFERENCES project(id) ON DELETE CASCADE, title TEXT NOT NULL, description TEXT,
  status VARCHAR(12) NOT NULL DEFAULT 'TODO' CHECK (status IN ('TODO','IN_PROGRESS','IN_REVIEW','DONE')),
  priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM' CHECK (priority IN ('LOW','MEDIUM','HIGH','CRITICAL')),
  assignee_id UUID REFERENCES users(id), reporter_id UUID REFERENCES users(id),
  start_date DATE, due_date DATE, position DOUBLE PRECISION NOT NULL DEFAULT 0,
  recurrence_rule TEXT, recurrence_source_id UUID REFERENCES task(id), completed_at TIMESTAMPTZ, deleted_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE INDEX ix_task_board ON task (project_id, status, position);
CREATE INDEX ix_task_assignee ON task (assignee_id, due_date);
CREATE INDEX ix_task_org_due ON task (org_id, due_date);
CREATE TABLE subtask (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), task_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE,
  title TEXT NOT NULL, done BOOLEAN NOT NULL DEFAULT FALSE, position INT NOT NULL DEFAULT 0);
CREATE TABLE task_dependency (
  task_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE, depends_on_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE,
  PRIMARY KEY (task_id, depends_on_id), CHECK (task_id <> depends_on_id));
CREATE TABLE label (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  name TEXT NOT NULL, color VARCHAR(9) NOT NULL DEFAULT '#6366f1', UNIQUE (org_id, name));
CREATE TABLE task_label (
  task_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE, label_id UUID NOT NULL REFERENCES label(id) ON DELETE CASCADE,
  PRIMARY KEY (task_id, label_id));
CREATE TABLE comment (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), task_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE,
  author_id UUID NOT NULL REFERENCES users(id), parent_id UUID REFERENCES comment(id), body TEXT NOT NULL,
  edited_at TIMESTAMPTZ, deleted_at TIMESTAMPTZ, created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE comment_mention (
  comment_id UUID NOT NULL REFERENCES comment(id) ON DELETE CASCADE, mentioned_user_id UUID NOT NULL REFERENCES users(id),
  PRIMARY KEY (comment_id, mentioned_user_id));
CREATE TABLE attachment (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  task_id UUID NOT NULL REFERENCES task(id) ON DELETE CASCADE, uploader_id UUID NOT NULL REFERENCES users(id),
  object_key TEXT NOT NULL, file_name TEXT NOT NULL, content_type TEXT NOT NULL, size_bytes BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE notification (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE, type VARCHAR(30) NOT NULL,
  title TEXT NOT NULL, body TEXT, entity_type VARCHAR(20), entity_id UUID, read_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE INDEX ix_notif_user ON notification (user_id, read_at, created_at DESC);
CREATE TABLE activity_log (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(), org_id UUID NOT NULL REFERENCES organization(id) ON DELETE CASCADE,
  project_id UUID REFERENCES project(id) ON DELETE CASCADE, task_id UUID REFERENCES task(id) ON DELETE CASCADE,
  actor_id UUID REFERENCES users(id), action VARCHAR(40) NOT NULL, details JSONB, created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE INDEX ix_activity_task ON activity_log (task_id, created_at);
CREATE INDEX ix_activity_org ON activity_log (org_id, created_at);
