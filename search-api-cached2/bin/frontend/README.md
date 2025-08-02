# Search Engine Frontend

A modern, responsive React frontend for the Search API backend. This application provides a beautiful search interface with autocomplete, instant answers, and image search capabilities.

## Features

- 🔍 **Smart Search Bar** with autocomplete suggestions
- ⚡ **Instant Answers** from DuckDuckGo API
- 🖼️ **Image Search** results with grid layout
- 📱 **Responsive Design** that works on all devices
- 🎨 **Modern UI** with smooth animations and gradients
- 📊 **Tabbed Results** to organize different types of content

## Prerequisites

- Node.js (version 16 or higher)
- npm or yarn
- Backend API running on `http://localhost:8080`

## Installation

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

## Running the Application

1. Start the development server:
   ```bash
   npm run dev
   ```

2. Open your browser and navigate to `http://localhost:5173`

3. Make sure your backend API is running on `http://localhost:8080`

## API Endpoints Used

The frontend connects to the following backend endpoints:

- `GET /api/search` - Search for web results
- `GET /api/autoComplete` - Get search suggestions
- `GET /api/search/ddg-instant` - Get instant answers
- `GET /api/search/images` - Search for images

## Project Structure

```
frontend/
├── src/
│   ├── App.jsx          # Main application component
│   ├── App.css          # Styles for the application
│   ├── index.css        # Global styles
│   └── main.jsx         # Application entry point
├── public/              # Static assets
└── package.json         # Dependencies and scripts
```

## Features in Detail

### Search Bar
- Real-time autocomplete suggestions
- Debounced input to prevent excessive API calls
- Enter key to trigger search
- Click suggestions to auto-fill and search

### Results Display
- **All Results Tab**: Shows everything in one view
- **Instant Answer Tab**: Displays quick answers and related topics
- **Images Tab**: Grid layout of image search results

### Responsive Design
- Mobile-first approach
- Adaptive layout for different screen sizes
- Touch-friendly interface

## Customization

### Changing API Base URL
To connect to a different backend URL, modify the `API_BASE_URL` constant in `src/App.jsx`:

```javascript
const API_BASE_URL = 'http://your-backend-url:port/api'
```

### Styling
The application uses CSS for styling. Main styles are in `src/App.css`. You can customize:
- Colors and gradients
- Layout and spacing
- Animations and transitions
- Responsive breakpoints

## Troubleshooting

### CORS Issues
If you encounter CORS errors, ensure your backend has the correct CORS configuration. The backend should allow requests from `http://localhost:5173`.

### API Connection Issues
- Verify the backend is running on `http://localhost:8080`
- Check browser console for network errors
- Ensure all API endpoints are accessible

### Build for Production
To create a production build:

```bash
npm run build
```

The built files will be in the `dist/` directory.

## Technologies Used

- **React 18** - UI framework
- **Vite** - Build tool and dev server
- **CSS3** - Styling with modern features
- **Fetch API** - HTTP requests

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is part of the Search Engine application.
