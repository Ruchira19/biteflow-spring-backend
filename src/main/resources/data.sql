-- Sample Data for Food Ordering System

-- Insert default admin user (password: admin123)
INSERT INTO users (full_name, email, password, role)
SELECT 'admin', 'admin@biteflow.com', '$2a$10$uTYr9CM3smaUCjfVKZU3n.mQ9/FA70tsVaH4TIPst2za99C4zi3Gu', 'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@biteflow.com'
);

-- Insert default customer user (password: customer123)
INSERT INTO users (full_name, email, password, role)
SELECT 'customer', 'customer@biteflow.com', '$2a$10$4S4p6nxo4SIVLjwDp242UO8cYLmd2unw4rDBbkrlIW1T63561Gjsa', 'CUSTOMER'
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'customer@biteflow.com'
);

-- Insert food categories
INSERT INTO category (name, description)
SELECT 'Burgers', 'Fresh grilled burgers and sandwiches'
WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE name = 'Burgers'
);

INSERT INTO category (name, description)
SELECT 'Pizzas', 'Authentic Italian and specialty pizzas'
WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE name = 'Pizzas'
);

INSERT INTO category (name, description)
SELECT 'Beverages', 'Cold and hot drinks'
WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE name = 'Beverages'
);

INSERT INTO category (name, description)
SELECT 'Desserts', 'Sweet treats and desserts'
WHERE NOT EXISTS (
    SELECT 1 FROM category WHERE name = 'Desserts'
);

-- Insert food items with sample data
INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Classic Beef Burger', 'Juicy beef patty with cheese, lettuce, tomato and special sauce', 450, 50, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Burgers'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Classic Beef Burger');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Chicken Burger', 'Crispy fried chicken breast with coleslaw and mayo', 600, 45, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Burgers'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Chicken Burger');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Vegetarian Burger', 'Plant-based patty with fresh vegetables and hummus', 370, 30, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Burgers'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Vegetarian Burger');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Margherita Pizza - Medium', 'Fresh mozzarella, basil, and tomato sauce on crispy crust', 2100, 25, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Pizzas'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Margherita Pizza');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Pepperoni Pizza - Medium', 'Classic pepperoni with mozzarella and tomato sauce', 3000, 30, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Pizzas'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Pepperoni Pizza');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Iced Lemon Tea', 'Freshly brewed tea with fresh lemon juice and ice', 100, 80, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Beverages'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Iced Lemon Tea');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Fresh Orange Juice', 'Freshly squeezed orange juice', 200, 60, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Beverages'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Fresh Orange Juice');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Chocolate Cake', 'Rich chocolate cake with chocolate frosting', 1500, 20, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Desserts'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Chocolate Cake');

INSERT INTO food_item (name, description, price, stock_quantity, status, category_id)
SELECT 'Cheesecake', 'New York style cheesecake with berry topping', 800, 15, 'AVAILABLE', c.id
FROM category c
WHERE c.name = 'Desserts'
  AND NOT EXISTS (SELECT 1 FROM food_item WHERE name = 'Cheesecake');

-- Create default carts for users
INSERT INTO cart (user_id)
SELECT u.id FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM cart c WHERE c.user_id = u.id
)
AND u.role = 'CUSTOMER';
