package utils;

import dtos.response.PostResponseDTO;

import java.util.Comparator;
import java.util.List;

/**
 * Utility class implementing sorting algorithms for blog posts
 * Demonstrates DSA concepts: QuickSort, Comparators
 */
public class SortingUtils {

    /**
     * QuickSort implementation for sorting posts
     */
    public static <T> void quickSort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1) {
            return;
        }
        quickSortHelper(list, 0, list.size() - 1, comparator);
    }

    private static <T> void quickSortHelper(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comparator);
            quickSortHelper(list, low, pivotIndex - 1, comparator);
            quickSortHelper(list, pivotIndex + 1, high, comparator);
        }
    }

    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        T pivot = list.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }

        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    /**
     * Sort posts by title (ascending, case-insensitive)
     */
    public static Comparator<PostResponseDTO> byTitleAscending() {
        return (p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle());
    }

    /**
     * Sort posts by title (descending, case-insensitive)
     */
    public static Comparator<PostResponseDTO> byTitleDescending() {
        return (p1, p2) -> p2.getTitle().compareToIgnoreCase(p1.getTitle());
    }

    /**
     * Sort posts by author (ascending, case-insensitive)
     */
    public static Comparator<PostResponseDTO> byAuthorAscending() {
        return (p1, p2) -> p1.getAuthor().compareToIgnoreCase(p2.getAuthor());
    }

    /**
     * Sort posts by author (descending, case-insensitive)
     */
    public static Comparator<PostResponseDTO> byAuthorDescending() {
        return (p1, p2) -> p2.getAuthor().compareToIgnoreCase(p1.getAuthor());
    }

    /**
     * Sort posts by date (newest first)
     */
    public static Comparator<PostResponseDTO> byDateNewestFirst() {
        return (p1, p2) -> p2.getLastUpdated().compareTo(p1.getLastUpdated());
    }

    /**
     * Sort posts by date (oldest first)
     */
    public static Comparator<PostResponseDTO> byDateOldestFirst() {
        return Comparator.comparing(PostResponseDTO::getLastUpdated);
    }

    /**
     * Sort posts by post ID (ascending)
     */
    public static Comparator<PostResponseDTO> byIdAscending() {
        return Comparator.comparingInt(PostResponseDTO::getPostId);
    }

    /**
     * Sort posts by post ID (descending)
     */
    public static Comparator<PostResponseDTO> byIdDescending() {
        return (p1, p2) -> Integer.compare(p2.getPostId(), p1.getPostId());
    }


    /**
     * Binary search for a post by ID in a sorted list
     *
     * @param sortedList List sorted by post ID
     * @param targetId   Target post ID to find
     * @return Index of the post, or -1 if not found
     */
    public static int binarySearchById(List<PostResponseDTO> sortedList, int targetId) {
        int left = 0;
        int right = sortedList.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midId = sortedList.get(mid).getPostId();

            if (midId == targetId) {
                return mid;
            } else if (midId < targetId) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    /**
     * Convenience method: Sort posts by a given criterion
     */
    public static void sortPosts(List<PostResponseDTO> posts, String sortBy, boolean ascending) {
        Comparator<PostResponseDTO> comparator = switch (sortBy.toLowerCase()) {
            case "title" -> ascending ? byTitleAscending() : byTitleDescending();
            case "author" -> ascending ? byAuthorAscending() : byAuthorDescending();
            case "date" -> ascending ? byDateOldestFirst() : byDateNewestFirst();
            case "id" -> ascending ? byIdAscending() : byIdDescending();
            default -> byDateNewestFirst(); // Default: newest first
        };

        quickSort(posts, comparator);
    }
}