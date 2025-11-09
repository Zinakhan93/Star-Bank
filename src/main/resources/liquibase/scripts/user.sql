-- liquibase formatted sql

-- changeset :001
-- comment Создание таблицы для хранения динамических правил рекомендаций

CREATE TABLE dynamic_rules (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               product_name VARCHAR(255) NOT NULL,
                               product_id VARCHAR(255) NOT NULL UNIQUE,
                               product_text TEXT,
                               rule_queries TEXT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- changeset 2
-- comment Создание индексов для таблицы dynamic_rules

CREATE UNIQUE INDEX idx_dynamic_rules_product_id ON dynamic_rules(product_id);
CREATE INDEX idx_dynamic_rules_created_at ON dynamic_rules(created_at);

-- changeset 3
-- comment Добавление комментариев к таблице и колонкам

COMMENT ON TABLE dynamic_rules IS 'Таблица для хранения динамических правил рекомендаций продуктов';
COMMENT ON COLUMN dynamic_rules.id IS 'Уникальный идентификатор правила';
COMMENT ON COLUMN dynamic_rules.product_name IS 'Название рекомендуемого продукта';
COMMENT ON COLUMN dynamic_rules.product_id IS 'UUID рекомендуемого продукта';
COMMENT ON COLUMN dynamic_rules.product_text IS 'Текст описания продукта для рекомендации';
COMMENT ON COLUMN dynamic_rules.rule_queries IS 'JSON массив с запросами правила в формате: [{"query": "USER_OF", "arguments": ["CREDIT"], "negate": true}, ...]';
COMMENT ON COLUMN dynamic_rules.created_at IS 'Дата и время создания правила';
COMMENT ON COLUMN dynamic_rules.updated_at IS 'Дата и время последнего обновления правила';