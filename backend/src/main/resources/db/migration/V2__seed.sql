-- ============================================================
-- Shopease V2 — Seed Data
-- Categories, Products (LUXE catalog), Users
-- ============================================================

-- -----------------------------------------------------------
-- Categories
-- -----------------------------------------------------------
INSERT INTO categories (name, slug, description, image_url, parent_id) VALUES
('Electronics',     'electronics',  'Premium consumer electronics and audio',      'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500', NULL),
('Audio',           'audio',        'High-fidelity headphones and speakers',        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500', NULL),
('Accessories',     'accessories',  'Leather goods, bags and personal accessories', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500', NULL),
('Watches',         'watches',      'Precision timepieces and wristwear',           'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=500', NULL),
('Home Decor',      'home-decor',   'Artisan ceramics, lighting and furnishings',   'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=500', NULL),
('Furniture',       'furniture',    'Curated furniture for modern living',           'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=500', NULL),
('Beauty',          'beauty',       'Luxury skincare and wellness products',         'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500', NULL);

-- -----------------------------------------------------------
-- Users
--   password = 'password123'  (BCrypt hash)
-- -----------------------------------------------------------
INSERT INTO users (name, email, password, role, active) VALUES
('Admin User',      'admin@shopease.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN',   1),
('Julian Smith',    'julian@example.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER',1),
('Marcus Wright',   'marcus@example.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER',1),
('Elena Lopez',     'elena@example.com',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER',1);

-- -----------------------------------------------------------
-- Products — LUXE Catalog
-- -----------------------------------------------------------
INSERT INTO products (name, slug, sku, summary, description, price, compare_at_price, stock, image_url, is_active, rating, review_count, category_id) VALUES

-- Electronics
('Linear Arch Lamp',               'linear-arch-lamp',                'LT-2024-001',
 'Minimalist desk lamp with matte black articulated design.',
 'A high-end architectural desk lamp with a sleek matte black finish and a minimalist articulated design. The product is photographed in a high-key studio setting with sharp, clean shadows, highlighting premium materials and precision engineering.',
 249.00, NULL,       85,
 'https://images.unsplash.com/photo-1507473885765-e6ed057ab6fe?w=500',
 1, 4.9, 42, 1),

-- Audio
('Signature H1 Headphones',        'signature-h1-headphones',         'AU-2024-001',
 'Premium wireless headphones in silver and cream.',
 'A premium wireless headphone set in a sophisticated silver and cream colorway, resting on a minimalist marble surface. The lighting is bright and ethereal, accentuating polished metallic hinges and soft leather ear cushions.',
 399.00, NULL,       120,
 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500',
 1, 5.0, 78, 2),

-- Accessories
('Nomad Leather Backpack',         'nomad-leather-backpack',          'AC-2024-001',
 'Sleek leather backpack in deep espresso brown.',
 'A sleek, minimalist leather backpack in a deep espresso brown, displayed against a light gray textured concrete wall. The design is clean with hidden zippers and a structured silhouette, conveying professional, urban, and quietly luxurious style.',
 185.00, NULL,       200,
 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500',
 1, 4.8, 63, 3),

('Signature Leather Tote',         'signature-leather-tote',          'AC-2024-002',
 'Midnight edition pebble-grain leather tote.',
 'A minimalist product shot of a high-end leather tote bag in deep midnight blue. The item is positioned against a clean, neutral grey studio background with professional softbox lighting that highlights the texture of the pebble-grain leather.',
 850.00, NULL,       45,
 'https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=500',
 1, 4.7, 31, 3),

-- Watches
('Orbital V2 Titanium',            'orbital-v2-titanium',             'WT-2024-001',
 'Brushed titanium case with black mesh strap.',
 'A minimalist designer watch featuring a brushed titanium case and a black mesh strap, set against a dark obsidian background with a single dramatic spotlight. The color palette is monochromatic black and silver, conveying absolute precision and elite craftsmanship.',
 520.00, 650.00,   30,
 'https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=500',
 1, 4.9, 55, 4),

('Chronos Elite Watch',            'chronos-elite-watch',             'WT-2024-002',
 'Brushed steel bracelet with deep emerald face.',
 'Close-up architectural shot of a luxury watch with a brushed steel bracelet and a deep emerald face. Macro photography focusing on the precision of the dial and hands, with dramatic lighting highlighting metallic edges against a soft dark background.',
 2400.00, NULL,      8,
 'https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=500',
 1, 4.8, 19, 4),

-- Home Decor
('Artisan Ceramic Vase',           'artisan-ceramic-vase',            'HM-2024-001',
 'Limited edition matte white organic-shaped vase.',
 'A high-end artisan ceramic vase with an irregular, organic shape and a matte white glaze. The vase is displayed on a minimalist wooden pedestal with soft shadows falling across its surface, reflecting the sophisticated and premium nature of the LUXE home collection.',
 185.00, NULL,       60,
 'https://images.unsplash.com/photo-1612196808214-b8e1d6145a8c?w=500',
 1, 4.6, 27, 5),

-- Furniture
('Velvet Lounge Chair',            'velvet-lounge-chair',             'FR-2024-001',
 'Premium velvet armchair in forest green.',
 'Stylized product photography of a premium velvet armchair in a rich forest green. The chair is placed in a minimalist interior with concrete walls and a single designer lamp casting warm, directional light. The focus is on the luxurious fabric texture and sleek dark wooden legs.',
 1250.00, NULL,      22,
 'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=500',
 1, 4.7, 18, 6),

-- Beauty
('Hydra-Restore Serum',            'hydra-restore-serum',             'BT-2024-001',
 'Frosted glass luxury skincare serum.',
 'A luxury skincare product arrangement featuring frosted glass bottles with minimalist black typography. The setting is a serene spa-like environment with soft ambient lighting and subtle reflections on a polished stone surface.',
 120.00, NULL,       150,
 'https://images.unsplash.com/photo-1596462502278-27bfdc403348?w=500',
 1, 4.5, 89, 7),

('Midnight Eau de Parfum',        'midnight-eau-de-parfum',          'BT-2024-002',
 'Woody oriental fragrance in obsidian bottle.',
 'An artisan fragrance bottle in dark obsidian glass, with clean minimalist typography. The image evokes mystery and premium quality through low-key lighting and deep charcoal tones.',
 195.00, NULL,       95,
 'https://images.unsplash.com/photo-1541643600914-78b084683601?w=500',
 1, 4.8, 44, 7),

-- Electronics (more)
('Sculpt Desktop Monitor',          'sculpt-desktop-monitor',          'LT-2024-002',
 'Ultra-thin 4K display with brushed aluminium bezel.',
 'A minimalist ultra-thin monitor in brushed aluminium, displayed on a clean white desk. The image emphasises the industrial precision and premium build quality expected of high-end consumer electronics.',
 899.00, NULL,       35,
 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=500',
 1, 4.7, 22, 1),

-- Audio (more)
('Studio Pro Earbuds',             'studio-pro-earbuds',              'AU-2024-002',
 'Noise-cancelling true wireless with ceramic case.',
 'Compact true-wireless earbuds in a matte ceramic charging case, photographed against a warm neutral background. The image highlights the premium materials and compact form factor.',
 299.00, NULL,       180,
 'https://images.unsplash.com/photo-1590658268037-6bf12f032f55?w=500',
 1, 4.6, 51, 2),

-- Accessories (more)
('Obsidian Card Holder',           'obsidian-card-holder',            'AC-2024-003',
 'Slim carbon-fibre card holder in matte black.',
 'A sleek carbon-fibre card holder in matte black, photographed on a dark stone surface. Clean lines and a minimalist silhouette convey modern sophistication.',
 145.00, NULL,       260,
 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=500',
 1, 4.4, 37, 3),

-- Watches (more)
('Meridian Chronograph',           'meridian-chronograph',            'WT-2024-003',
 'Rose gold chronograph with sapphire crystal.',
 'A luxury chronograph in rose gold with a sapphire crystal face, set on a dark leather surface. The lighting captures the warmth of the metal and the precision of the sub-dials.',
 1850.00, NULL,      14,
 'https://images.unsplash.com/photo-1547996160-81dfa63595aa?w=500',
 1, 4.9, 12, 4),

-- Home Decor (more)
('Lunar Pendant Light',            'lunar-pendant-light',             'HM-2024-002',
 'Large brass pendant with opal glass shade.',
 'A statement brass pendant light with a hand-blown opal glass shade, hanging above a marble dining table. The warm glow and rich metallic finish create an atmosphere of quiet luxury.',
 475.00, NULL,       40,
 'https://images.unsplash.com/photo-1524484485831-a92ffc0de03f?w=500',
 1, 4.8, 15, 5),

-- Furniture (more)
('Marble Side Table',              'marble-side-table',               'FR-2024-003',
 'Carrara marble top with brass legs.',
 'A sculptural side table with a Carrara marble top and slim brass legs, photographed in a bright minimalist interior. The clean geometry and natural stone veining exemplify modern luxury design.',
 680.00, NULL,       18,
 'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=500',
 1, 4.7, 9, 6);
