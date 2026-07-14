import keychain from "../assets/keychain.jpeg";
import slimwallets from "../assets/slim-wallets.png";
import  wallet2 from "../assets/wallet2.jpg";
import wallet3 from "../assets/wallet3.jpg";
import wallet4 from"../assets/wallet4.avif";
import belt1 from"../assets/belt1.jpg";
import belt2 from"../assets/belt2.jpg";
import belt3 from"../assets/belt3.jpg";
import belt4 from"../assets/belt4.jpg";
import bracelet1 from"../assets/bracelet1.webp";
import bracelet2 from"../assets/bracelet2.webp";
import bracelet3 from"../assets/bracelet3.webp";
import bracelet4 from"../assets/bracelet4.webp";
import bracelet5 from"../assets/bracelet5.webp";
import bracelet6 from"../assets/bracelet6.webp";
import bag1 from"../assets/bag1.jpg";
import bag2 from"../assets/bag2.webp";
import tie1 from"../assets/tie1.jpg";
import tie2 from"../assets/tie2.jpg";
import tie3 from"../assets/tie3.jpeg";
import tie4 from"../assets/tie4.jpg";
import tie5 from"../assets/tie5.jpg";
import tie6 from"../assets/tie6.webp";
import perfume from"../assets/perfume.jpg";
import shoe from"../assets/shoe.avif";
import ring1 from "../assets/ring1.webp";
import ring2 from "../assets/ring2.webp";
import ring3 from "../assets/ring3.jpg";
import ring4 from "../assets/ring4.webp";
import ring5 from "../assets/ring5.webp";
import ring6 from "../assets/ring6.jpg";
import key1 from "../assets/key1.webp";
import key2 from "../assets/key2.jpg";
import key3 from "../assets/key3.jpg";
import key4 from "../assets/key4.jpeg";
import key5 from "../assets/key5.webp";
import watch1 from"../assets/watch1.jpg";


const productData = [
  {
    id: 1,
    name: "Men's Wallet",
    image: "https://images.unsplash.com/photo-1627123424574-724758594e93?w=500",
   related: [
  {
    id: 101,
    name: "Brown Leather Wallet",
    price: "₹799",
    image:
      "https://images.unsplash.com/photo-1627123424574-724758594e93?w=500",
  },
  {
    id: 102,
    name: "Black Slim Wallet",
    price: "₹699",
    image: wallet2,
  },
  {
    id: 103,
    name: "Classic Men Wallet",
    price: "₹899",
    image: wallet3,
  },
  {
    id: 104,
    name: "Premium Leather Wallet",
    price: "₹999",
    image: wallet4,
  },
  {
    id: 105,
    name: "Formal Wallet",
    price: "₹749",
    image:
      "https://images.unsplash.com/photo-1606503825008-909a67e63c3d?w=800",
  },
  {
    id: 106,
    name: "Leather Card Holder",
    price: "₹499",
    image: slimwallets,
  },
],
  },
  {
    id: 2,
    name: "Leather Belt",
    image: "https://www.leathertalks.com/cdn/shop/products/2022-11-2412-02-11_B_R8_S4.jpg?v=1669619026",
    related: [
      {
        id: 201,
        name: "Brown Leather Belt",
        price: "₹599",
        image: "https://www.leathertalks.com/cdn/shop/products/2022-11-2412-02-11_B_R8_S4.jpg?v=1669619026",
      },
      {
        id: 202,
        name: "Black Formal Belt",
        price: "₹699",
        image: belt1,
      },
      {
        id: 203,
        name: "Casual Belt",
        price: "₹499",
        image: "https://images.unsplash.com/photo-1624222247344-550fb60583dc?w=800",
      },
      {
        id: 204,
        name: "Office Belt",
        price: "₹799",
        image: belt2,
      },
      {
        id: 205,
        name: "Premium Buckle Belt",
        price: "₹899",
        image: belt3,
      },
      {
        id: 206,
        name: "Designer Belt",
        price: "₹999",
        image: belt4,
      },
    ],
  },
  {
    id: 3,
    name: "Sunglasses",
    image: "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=500",
    related: [
      {
        id: 301,
        name: "Black Sunglasses",
        price: "₹899",
        image: "https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=800",
      },
      {
        id: 302,
        name: "Round Sunglasses",
        price: "₹999",
        image: "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800",
      },
      {
        id: 303,
        name: "Aviator Sunglasses",
        price: "₹1199",
        image: "https://images.unsplash.com/photo-1509695507497-903c140c43b0?w=800",
      },
      {
        id: 304,
        name: "Brown Sunglasses",
        price: "₹799",
        image: "https://images.unsplash.com/photo-1577803645773-f96470509666?w=800",
      },
      {
        id: 305,
        name: "Fashion Sunglasses",
        price: "₹1099",
        image: "https://images.unsplash.com/photo-1556306535-38febf6782e7?w=800",
      },
      {
        id: 306,
        name: "UV Protected Glasses",
        price: "₹1299",
        image: "https://images.unsplash.com/photo-1563903530908-afdd155d057a?w=800",
      },
    ],
  },
  {
    id: 4,
    name: "Wrist Watch",
    image: "https://cdn.theluxurycloset.com/uploads/opt/products/750x750/luxury-men-rolex-new-watches-p671924-009.jpeg",
    related: [
      {
        id: 401,
        name: " Rolex Watch",
        price: "₹24999",
        image: watch1,
      },
      {
        id: 402,
        name: "Leather  Watch",
        price: "₹1999",
        image: "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=800",
      },
      {
        id: 403,
        name: "Smart Watch",
        price: "₹2999",
        image: "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=800",
      },
      {
        id: 404,
        name: "Metal Strap Watch",
        price: "₹1799",
        image: "https://images.unsplash.com/photo-1434056886845-dac89ffe9b56?w=800",
      },
      {
        id: 405,
        name: "Black Watch",
        price: "₹2299",
        image: "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800",
      },
      {
        id: 406,
        name: "Silver Watch",
        price: "₹1599",
        image: "https://images.unsplash.com/photo-1523170335258-f5ed11844a49?w=800",
      },
    ],
  },
  {
    id: 5,
    name: "Bracelet",
    image: "https://i.ebayimg.com/images/g/iM4AAOSwV2JiHCEN/s-l1200.jpg",
    related: [
      {
        id: 501,
        name: "Silver Bracelet",
        price: "₹699",
        image: bracelet1,
      },
      {
        id: 502,
        name: "Leather Bracelet",
        price: "₹799",
        image: bracelet2,
      },
      {
        id: 503,
        name: "chain Bracelet",
        price: "₹499",
        image: bracelet3,
      },
      {
        id: 504,
        name: "Men Bracelet",
        price: "₹599",
        image: bracelet4,
      },
      {
        id: 505,
        name: "Premium Bracelet",
        price: "₹899",
        image: bracelet5,
      },
      {
        id: 506,
        name: "stylish Bracelet",
        price: "₹999",
        image: bracelet6,
      },
    ],
  },
  {
    id: 6,
    name: "Cap",
    image: "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=500",
    related: [
      {
        id: 601,
        name: "Black Cap",
        price: "₹399",
        image: "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=800",
      },
      {
        id: 602,
        name: "Sports Cap",
        price: "₹499",
        image: "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=800",
      },
      {
        id: 603,
        name: "Casual Cap",
        price: "₹349",
        image: "https://images.unsplash.com/photo-1529958030586-3aae4ca485ff?w=800",
      },
      {
        id: 604,
        name: "Cotton Cap",
        price: "₹449",
        image: "https://images.unsplash.com/photo-1575428652377-a2d80e2277fc?w=800",
      },
      {
        id: 605,
        name: "Denim Cap",
        price: "₹599",
        image: "https://images.unsplash.com/photo-1534215754734-18e55d13e346?w=800",
      },
      {
        id: 606,
        name: "Premium Cap",
        price: "₹699",
        image: "https://images.unsplash.com/photo-1560774358-d727658f457c?w=800",
      },
    ],
  },
  {
    id: 7,
    name: "Backpack",
    image: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=500",
    related: [
      {
        id: 701,
        name: "Travel Backpack",
        price: "₹1499",
        image: "https://images.unsplash.com/photo-1622260614153-03223fb72052?w=800",
      },
      {
        id: 702,
        name: "Laptop Backpack",
        price: "₹1799",
        image: bag2,
      },
      {
        id: 703,
        name: "Office Backpack",
        price: "₹1299",
        image: bag1,
      },
      {
        id: 704,
        name: "School Backpack",
        price: "₹999",
        image: "https://images.unsplash.com/photo-1581605405669-fcdf81165afa?w=800",
      },
      {
        id: 705,
        name: "Casual Backpack",
        price: "₹1199",
        image: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
      },
      {
        id: 706,
        name: "Premium Backpack",
        price: "₹1999",
        image: "https://images.unsplash.com/photo-1577733966973-d680bffd2e80?w=800",
      },
    ],
  },
  {
    id: 8,
    name: "Tie",
    image: "https://images.unsplash.com/photo-1589756823695-278bc923f962?w=800",
    related: [
      {
        id: 801,
        name: "Black Tie",
        price: "₹399",
        image: tie1,
      },
      {
        id: 802,
        name: "Blue Formal Tie",
        price: "₹449",
        image: tie2,
      },
      {
        id: 803,
        name: "Silk Tie",
        price: "₹699",
        image: tie3,
      },
      {
        id: 804,
        name: "Wedding Tie",
        price: "₹799",
        image: tie4,
      },
      {
        id: 805,
        name: "Office Tie",
        price: "₹499",
        image: tie5,
      },
      {
        id: 806,
        name: "Premium Tie",
        price: "₹899",
        image: tie6,
      },
    ],
  },
  {
    id: 9,
    name: "Perfume",
    image: "https://images.unsplash.com/photo-1541643600914-78b084683601?w=500",
    related: [
      {
        id: 901,
        name: "Luxury Perfume",
        price: "₹1299",
        image: "https://images.unsplash.com/photo-1541643600914-78b084683601?w=800",
      },
      {
        id: 902,
        name: "Men Perfume",
        price: "₹999",
        image: "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=800",
      },
      {
        id: 903,
        name: "Fresh Perfume",
        price: "₹899",
        image: "https://images.unsplash.com/photo-1594035910387-fea47794261f?w=800",
      },
      {
        id: 904,
        name: "Office Perfume",
        price: "₹1099",
        image: perfume,
      },
      {
        id: 905,
        name: "Premium Perfume",
        price: "₹1499",
        image: "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=800",
      },
      {
        id: 906,
        name: "Daily Perfume",
        price: "₹799",
        image: "https://images.unsplash.com/photo-1595425970377-c9703cf48b6d?w=800",
      },
    ],
  },
  {
    id: 10,
    name: "Keychain",
    image: keychain,
    related: [
      {
        id: 1001,
        name: "Leather Keychain",
        price: "₹199",
        image: keychain,
      },
      {
        id: 1002,
        name: "Metal Keychain",
        price: "₹249",
        image: key1,
      },
      {
        id: 1003,
        name: "Car Keychain",
        price: "₹299",
        image: key2,
      },
      {
        id: 1004,
        name: "Name Keychain",
        price: "₹349",
        image: key3,
      },
      {
        id: 1005,
        name: "Gift Keychain",
        price: "₹399",
        image: key4,
      },
      {
        id: 1006,
        name: "Premium Keychain",
        price: "₹499",
        image: key5,
      },
    ],
  },
  {
    id: 11,
    name: "Shoes",
    image: "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=500",
    related: [
      {
        id: 1101,
        name: "Casual Shoes",
        price: "₹1499",
        image: "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=800",
      },
      {
        id: 1102,
        name: "Formal Shoes",
        price: "₹1999",
        image: shoe,
      },
      {
        id: 1103,
        name: "Running Shoes",
        price: "₹1799",
        image: "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
      },
      {
        id: 1104,
        name: "Leather Shoes",
        price: "₹2499",
        image: "https://images.unsplash.com/photo-1616406432452-07bc5938759d?w=800",
      },
      {
        id: 1105,
        name: "Sneakers",
        price: "₹1599",
        image: "https://images.unsplash.com/photo-1600185365483-26d7a4cc7519?w=800",
      },
      {
        id: 1106,
        name: "Sports Shoes",
        price: "₹1899",
        image: "https://images.unsplash.com/photo-1543508282-6319a3e2621f?w=800",
      },
    ],
  },
  {
    id: 12,
    name: "Ring",
    image: "https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500",
    related: [
      {
        id: 1201,
        name: "Silver Ring",
        price: "₹499",
        image: ring1,
      },
      {
        id: 1202,
        name: "Gold Ring",
        price: "₹999",
        image: ring2,
      },
      {
        id: 1203,
        name: "Men Ring",
        price: "₹799",
        image: ring3,
      },
      {
        id: 1204,
        name: "Fashion Ring",
        price: "₹699",
        image: ring4,
      },
      {
        id: 1205,
        name: "Couple Ring",
        price: "₹1299",
        image: ring5,
      },
      {
        id: 1206,
        name: "Premium Ring",
        price: "₹1499",
        image: ring6,
      },
    ],
  },
];

export default productData;