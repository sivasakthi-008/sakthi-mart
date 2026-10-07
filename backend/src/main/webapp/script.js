// ===============================
// SAKTHI MART - MAIN SCRIPT
// ===============================

const API_BASE = "api";

const fallbackProducts = [
    {
        id: 1,
        name: "Handcrafted Beaded Bracelet",
        price: 299,
        oldPrice: 399,
        rating: 4.7,
        category: "Jewellery",
        imageUrl: "assets/bracelet.jpg"
    },
    {
        id: 2,
        name: "Traditional Clay Pot",
        price: 449,
        oldPrice: 599,
        rating: 4.8,
        category: "Pottery",
        imageUrl: "assets/pot.jpg"
    },
    {
        id: 3,
        name: "Natural Jute Basket",
        price: 549,
        oldPrice: 699,
        rating: 4.6,
        category: "Bags",
        imageUrl: "assets/basket.jpg"
    },
    {
        id: 4,
        name: "Handmade Wall Decor",
        price: 799,
        oldPrice: 999,
        rating: 4.9,
        category: "Decor",
        imageUrl: "assets/decor-product.jpg"
    },
    {
        id: 5,
        name: "Artisan Necklace",
        price: 699,
        oldPrice: 899,
        rating: 4.7,
        category: "Jewellery",
        imageUrl: "assets/necklace.jpg"
    },
    {
        id: 6,
        name: "Handcrafted Ceramic Vase",
        price: 599,
        oldPrice: 799,
        rating: 4.8,
        category: "Decor",
        imageUrl: "assets/vase.jpg"
    },
    {
        id: 7,
        name: "Handmade Gift Box",
        price: 399,
        oldPrice: 499,
        rating: 4.5,
        category: "Decor",
        imageUrl: "assets/gift.jpg"
    },
    {
        id: 8,
        name: "Artisan Tote Bag",
        price: 649,
        oldPrice: 799,
        rating: 4.6,
        category: "Bags",
        imageUrl: "assets/tote.jpg"
    }
];

let products = [];
let cart = [];


// ===============================
// LOAD PRODUCTS
// ===============================

async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE}/products`);

        if (!response.ok) {
            throw new Error("Product API failed");
        }

        const data = await response.json();

        if (Array.isArray(data) && data.length > 0) {
            products = [...fallbackProducts, ...data.map(product => ({ ...product, price: Number(product.price), rating: Number(product.rating || 4.5), imageUrl: product.imageUrl || "assets/hero.jpg" }))];
        } else {
            products = fallbackProducts;
        }

    } catch (error) {
        console.log("Using demo products:", error);
        products = fallbackProducts;
    }

    displayProducts();
    updateCart();
}


// ===============================
// DISPLAY PRODUCTS
// ===============================

function displayProducts(productList = products) {

    const productGrid = document.getElementById("productGrid");

    if (!productGrid) return;

    productGrid.innerHTML = "";

    if (productList.length === 0) {
        productGrid.innerHTML = `
            <div class="empty-products">
                <h3>No products found</h3>
                <p>Try another search or category.</p>
            </div>
        `;
        return;
    }

    productList.forEach(product => {

        const card = document.createElement("div");

        card.className = "product-card";

        const image = product.imageUrl || product.image || "assets/hero.jpg";

        card.innerHTML = `
            <div class="product-image"
                 style="background-image:url('${image}')">
            </div>

            <div class="product-info">

                <span class="product-category">
                    ${product.category || "Handmade"}
                </span>

                <h3>${escapeHTML(product.name)}</h3>

                <div class="rating">
                     ${product.rating || "4.5"}
                </div>

                <div class="price-row">

                    <span class="price">
                        ${Number(product.price).toLocaleString("en-IN")}
                    </span>

                    ${
                        product.oldPrice
                            ? `<span class="old-price">
                                ${Number(product.oldPrice).toLocaleString("en-IN")}
                               </span>`
                            : ""
                    }

                </div>

                <button
                    class="add-cart"
                    onclick="addToCart(${product.id})">
                    Add to Cart
                </button>

            </div>
        `;

        productGrid.appendChild(card);
    });
}


// ===============================
// SEARCH
// ===============================

function searchProducts() {

    const searchInput = document.getElementById("searchInput");

    if (!searchInput) return;

    const searchText = searchInput.value
        .toLowerCase()
        .trim();

    if (!searchText) {
        displayProducts(products);
        return;
    }

    const filteredProducts = products.filter(product =>

        product.name.toLowerCase().includes(searchText) ||

        (product.category &&
            product.category.toLowerCase().includes(searchText)) ||

        (product.description &&
            product.description.toLowerCase().includes(searchText))

    );

    displayProducts(filteredProducts);

    const productsSection = document.getElementById("products");

    if (productsSection) {
        productsSection.scrollIntoView({
            behavior: "smooth"
        });
    }
}


// ===============================
// CATEGORY SEARCH
// ===============================

function searchCategory(category) {

    const filteredProducts = products.filter(product => {

        const productCategory =
            (product.category || "").toLowerCase();

        const selectedCategory =
            category.toLowerCase();

        return productCategory.includes(selectedCategory);
    });

    displayProducts(filteredProducts);

    const productsSection = document.getElementById("products");

    if (productsSection) {
        productsSection.scrollIntoView({
            behavior: "smooth"
        });
    }
}


// ===============================
// SORT PRODUCTS
// ===============================

function sortProducts() {

    const sortSelect = document.getElementById("sortProducts");

    if (!sortSelect) return;

    const value = sortSelect.value;

    const sortedProducts = [...products];

    if (value === "low") {

        sortedProducts.sort(
            (a, b) => Number(a.price) - Number(b.price)
        );

    } else if (value === "high") {

        sortedProducts.sort(
            (a, b) => Number(b.price) - Number(a.price)
        );

    } else if (value === "rating") {

        sortedProducts.sort(
            (a, b) =>
                Number(b.rating || 0) -
                Number(a.rating || 0)
        );
    }

    displayProducts(sortedProducts);
}


// ===============================
// VIEW MORE
// ===============================

function scrollToProducts() {

    const productsSection =
        document.getElementById("products");

    if (productsSection) {
        productsSection.scrollIntoView({
            behavior: "smooth"
        });
    }
}


// ===============================
// CART
// ===============================

async function addToCart(productId) {

    const product = products.find(
        item => Number(item.id) === Number(productId)
    );

    if (!product) return;

    try {
        const formData = new URLSearchParams();

        formData.append("productId", productId);
        formData.append("quantity", "1");

        const response = await fetch(`${API_BASE}/cart`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.error || "Unable to add product to cart.");
            return;
        }

        const existingProduct = cart.find(
            item => Number(item.id) === Number(productId)
        );

        if (existingProduct) {
            existingProduct.quantity++;
        } else {
            cart.push({
                ...product,
                quantity: 1
            });
        }

        updateCart();
        openCart();

    } catch (error) {
        console.error(error);
        alert("Unable to connect to cart.");
    }
}

async function changeQuantity(productId, change) {

    const item = cart.find(
        product => Number(product.id) === Number(productId)
    );

    if (!item) return;

    const newQuantity = item.quantity + change;

    try {

        const formData = new URLSearchParams();

        formData.append("productId", productId);
        formData.append("quantity", newQuantity);

        const response = await fetch(`${API_BASE}/cart`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.error || "Unable to update cart.");
            return;
        }

        if (newQuantity <= 0) {

            cart = cart.filter(
                product => Number(product.id) !== Number(productId)
            );

        } else {

            item.quantity = newQuantity;
        }

        updateCart();

    } catch (error) {

        console.error(error);
        alert("Unable to update cart.");
    }
}

async function removeFromCart(productId) {

    try {

        const response = await fetch(
            `${API_BASE}/cart?productId=${productId}`,
            {
                method: "DELETE"
            }
        );

        const result = await response.json();

        if (!response.ok) {
            alert(result.error || "Unable to remove product.");
            return;
        }

        cart = cart.filter(
            product => Number(product.id) !== Number(productId)
        );

        updateCart();

    } catch (error) {

        console.error(error);

        alert("Unable to connect to server.");
    }
}
function updateCart() {

    const cartItems =
        document.getElementById("cartItems");

    const cartCount =
        document.getElementById("cartCount");

    const cartTotal =
        document.getElementById("cartTotal");

    if (!cartItems) return;

    let totalItems = 0;
    let totalPrice = 0;

    cart.forEach(item => {

        totalItems += item.quantity;

        totalPrice +=
            Number(item.price) * item.quantity;
    });

    if (cartCount) {
        cartCount.textContent = totalItems;
    }

    if (cartTotal) {
        cartTotal.textContent =
            totalPrice.toLocaleString("en-IN");
    }

    if (cart.length === 0) {

        cartItems.innerHTML = `
            <div class="empty-cart">
                <div class="empty-cart-icon"></div>
                <h3>Your cart is empty</h3>
                <p>Add some beautiful handmade products.</p>
            </div>
        `;

        return;
    }

    cartItems.innerHTML = "";

    cart.forEach(item => {

        const cartItem =
            document.createElement("div");

        cartItem.className = "cart-item";

        cartItem.innerHTML = `

    <div class="cart-item-image">
        <img
            src="${escapeHTML(item.imageUrl || "assets/hero.jpg")}"
            alt="${escapeHTML(item.name)}"
            onerror="this.src='assets/hero.jpg'"
        >
    </div>

    <div class="cart-item-info">

        <strong>
            ${escapeHTML(item.name)}
        </strong>

        <p>
            ${Number(item.price).toLocaleString("en-IN")}
        </p>

    </div>

            <div class="cart-controls">

                <button
    onclick="changeQuantity(${item.id}, -1)">
    −
</button>

                <span>
                    ${item.quantity}
                </span>

                <button
                    onclick="changeQuantity(${item.id}, 1)">
                    +
                </button>

                <button
                    class="remove-cart"
                    onclick="removeFromCart(${item.id})">
                    Remove
                </button>

            </div>
        `;

        cartItems.appendChild(cartItem);
    });
}


function openCart() {

    const cartPanel =
        document.getElementById("cartPanel");

    if (cartPanel) {
        cartPanel.classList.add("active");
    }
}


function closeCart() {

    const cartPanel =
        document.getElementById("cartPanel");

    if (cartPanel) {
        cartPanel.classList.remove("active");
    }
}


// ===============================
// LOGIN
// ===============================

function showLogin() {

    const modal =
        document.getElementById("loginModal");

    if (modal) {
        modal.classList.add("active");
    }
}


function closeLogin() {

    const modal =
        document.getElementById("loginModal");

    if (modal) {
        modal.classList.remove("active");
    }
}


async function loginUser() {

    const email =
        document.getElementById("loginEmail").value.trim();

    const password =
        document.getElementById("loginPassword").value.trim();

    if (!email || !password) {
        alert("Please enter email and password.");
        return;
    }

    try {

        const formData = new URLSearchParams();

        formData.append("email", email);
        formData.append("password", password);

        const response = await fetch(
            `${API_BASE}/auth/login`,
            {
                method: "POST",
                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },
                body: formData
            }
        );

        const result = await response.json();

        if (!response.ok) {
            alert(result.error || "Login failed.");
            return;
        }
        localStorage.setItem("userRole", result.role);
        if (result.role === "SELLER") {
    document.getElementById("sellerDashboard").style.display = "block";
    loadMyProducts();
}
        alert("Login successful! ");

        closeLogin();

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to server. Please try again."
        );
    }
}
function logoutUser() {
    localStorage.removeItem("userRole");

    const sellerDashboard =
        document.getElementById("sellerDashboard");

    if (sellerDashboard) {
        sellerDashboard.style.display = "none";
    }

    alert("Logged out successfully.");

    location.reload();
}


// ===============================
// CHECKOUT
// ===============================

async function checkout() {

    if (cart.length === 0) {

        alert("Your cart is empty.");

        return;
    }

    try {

        const response = await fetch(
            `${API_BASE}/orders`,
            {
                method: "POST"
            }
        );

        const result = await response.json();

        if (!response.ok) {

            alert(
                result.error ||
                "Unable to place order."
            );

            return;
        }

        alert(
            "Order placed successfully!\n" +
            "Order ID: " + result.orderId +
            "\nTotal: ₹" + result.totalAmount
        );

        cart = [];

        updateCart();

        closeCart();

    } catch (error) {

        console.error(error);

        alert(
            "Unable to connect to checkout."
        );
    }
}


// ===============================
// BACK TO TOP
// ===============================

function backToTop() {

    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
}


// ===============================
// ESCAPE HTML
// ===============================

function escapeHTML(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


// ===============================
// EVENTS
// ===============================

document.addEventListener("DOMContentLoaded", () => {

    loadProducts();

    const searchInput =
        document.getElementById("searchInput");

    if (searchInput) {

        searchInput.addEventListener(
            "keydown",
            event => {

                if (event.key === "Enter") {
                    searchProducts();
                }
            }
        );
    }

    const sortSelect =
        document.getElementById("sortProducts");

    if (sortSelect) {

        sortSelect.addEventListener(
            "change",
            sortProducts
        );
    }

});


// ===============================
// CLOSE MODAL WHEN CLICKING OUTSIDE
// ===============================

window.addEventListener("click", event => {

    const loginModal =
        document.getElementById("loginModal");

    if (
        loginModal &&
        event.target === loginModal
    ) {
        closeLogin();
    }

});

document.getElementById("sellerProductForm")?.addEventListener("submit", async function (event) {
    event.preventDefault();

    const formData = new URLSearchParams();

    formData.append("name", document.getElementById("productName").value.trim());
    formData.append("description", document.getElementById("productDescription").value.trim());
    formData.append("price", document.getElementById("productPrice").value);
    formData.append("stock", document.getElementById("productStock").value);
    formData.append("category", document.getElementById("productCategory").value);
    formData.append("imageUrl", document.getElementById("productImageUrl").value.trim());

    try {
        const response = await fetch(`${API_BASE}/products`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.error || "Unable to add product.");
            return;
        }

        alert("Product added successfully!");
        this.reset();
        loadProducts();

    } catch (error) {
        console.error(error);
        alert("Unable to connect to server.");
    }
});
function showRegister() {
    const modal = document.getElementById("registerModal");
    if (modal) {
        modal.classList.add("active");
    }
    closeLogin();
}

function closeRegister() {
    const modal = document.getElementById("registerModal");
    if (modal) {
        modal.classList.remove("active");
    }
}

function switchToLogin() {
    closeRegister();
    showLogin();
}

async function registerUser() {
    const name = document.getElementById("registerName").value.trim();
    const email = document.getElementById("registerEmail").value.trim();
    const password = document.getElementById("registerPassword").value.trim();
    const role = document.getElementById("registerRole").value;

    if (!name || !email || !password) {
        alert("Please fill all fields.");
        return;
    }

    try {
        const formData = new URLSearchParams();

        formData.append("name", name);
        formData.append("email", email);
        formData.append("password", password);
        formData.append("role", role);

        const response = await fetch(`${API_BASE}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: formData
        });

        const result = await response.json();

        if (!response.ok) {
            alert(result.error || "Registration failed.");
            return;
        }

        alert("Registration successful! Please login.");
        closeRegister();
        showLogin();

        document.getElementById("registerName").value = "";
        document.getElementById("registerEmail").value = "";
        document.getElementById("registerPassword").value = "";

    } catch (error) {
        console.error(error);
        alert("Unable to connect to server.");
    }
}
async function loadMyProducts() {
    const section = document.getElementById("myProductsSection");
    const list = document.getElementById("myProductsList");

    if (!section || !list) return;

    try {
        const response = await fetch(`${API_BASE}/products?view=mine`);
        const products = await response.json();

        section.style.display = "block";

        if (products.length === 0) {
            list.innerHTML = "<p>No products added yet.</p>";
            return;
        }

        list.innerHTML = products.map(product => `
            <div style="padding:15px; margin:10px 0; border:1px solid #ddd; border-radius:10px;">
                <h3>${product.name}</h3>
                <p>₹${product.price} | Stock: ${product.stock}</p>
                <button onclick="deleteMyProduct(${product.id})">Delete</button>
            </div>
        `).join("");

    } catch (error) {
        console.error(error);
    }
}

async function deleteMyProduct(id) {
    if (!confirm("Delete this product?")) return;

    const response = await fetch(
        `${API_BASE}/products?id=${id}`,
        { method: "DELETE" }
    );

    if (response.ok) {
        alert("Product deleted successfully.");
        loadMyProducts();
    } else {
        alert("Unable to delete product.");
    }
}

