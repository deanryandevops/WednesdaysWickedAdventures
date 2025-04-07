// Tab Switching Logic
const tabs = document.querySelectorAll('.tab-button');
const contents = document.querySelectorAll('.tab-content');

tabs.forEach(tab => {
  tab.addEventListener('click', () => {
    tabs.forEach(t => t.classList.remove('active'));
    contents.forEach(c => c.classList.remove('active'));
    
    tab.classList.add('active');
    document.getElementById(tab.dataset.tab).classList.add('active');
  });
});

// Booking Submission and Storage
const bookingForm = document.getElementById('bookingForm');
const adminTableBody = document.getElementById('adminTableBody');
let bookings = JSON.parse(localStorage.getItem('bookings')) || [];

function renderBookings() {
  adminTableBody.innerHTML = bookings.map((booking, index) => `
    <tr>
      <td>${booking.name}</td>
      <td>${booking.email}</td>
      <td>${booking.park}</td>
      <td>${booking.numberofticket}</td>
      <td></td>
      
    <!--
      <td><button class="delete-button" data-index="${index}">Delete</button></td>
      <td><button class="delete-button" data-index="${index}">Edit</button></td>
      -->

    </tr>
  
  `).join('');
}

bookingForm.addEventListener('submit', event => {
  event.preventDefault();
  const name = document.getElementById('name').value;
  const email = document.getElementById('email').value;
  const park = document.getElementById('park').value;
  const numberofticket = document.getElementById('numberofticket').value;

  bookings.push({ name, email, park,numberofticket });
  localStorage.setItem('bookings', JSON.stringify(bookings));
  renderBookings();

  alert('Booking submitted successfully!');
  bookingForm.reset();
});

adminTableBody.addEventListener('click', event => {
  if (event.target.classList.contains('delete-button')) {
    const index = event.target.dataset.index;
    bookings.splice(index, 1);
    localStorage.setItem('bookings', JSON.stringify(bookings));
    renderBookings();
  }
});

renderBookings();
