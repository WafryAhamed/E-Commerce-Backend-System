-- Optional sample data script (if you want SQL-based seed data)

INSERT INTO categories (name) VALUES
('Electronics'),
('Books'),
('Fashion')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (username, email, password, role)
VALUES
('admin', 'admin@shop.com', '$2a$10$rUAjHq7YAJ8sQnEztNQY6uUQ5NrCPlhM/Q7zHqz8m3tP8cxM4E2A2', 'ROLE_ADMIN'),
('customer', 'customer@shop.com', '$2a$10$1jrbfQK6X5wIOlQe6VxwM.RQwz7a1j6sW8bY9sv2fqqgJtAs4rD6e', 'ROLE_CUSTOMER')
ON CONFLICT (username) DO NOTHING;

INSERT INTO products (name, description, price, stock, category_id)
SELECT 'Wireless Headphones', 'Noise-cancelling over-ear headphones', 149.99, 100, c.id
FROM categories c WHERE c.name = 'Electronics'
ON CONFLICT DO NOTHING;

